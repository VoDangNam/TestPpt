package com.auctionaa.backend.Service;

import com.auctionaa.backend.DTO.Request.PlaceBidRequest;
import com.auctionaa.backend.DTO.Response.PlaceBidResponse;
import com.auctionaa.backend.DTO.Result.BidResult;
import com.auctionaa.backend.Entity.AuctionSession;
import com.auctionaa.backend.Entity.Bids;
import com.auctionaa.backend.Entity.Idempotency;
import com.auctionaa.backend.Entity.Wallet;
import com.mongodb.DuplicateKeyException;
import lombok.RequiredArgsConstructor;
import org.bson.types.Decimal128;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BidService {
    private final MongoTemplate mongo;
    private final SimpMessagingTemplate ws;

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final int STATUS_LIVE = 2;

    private static Decimal128 d128(BigDecimal v) { return new Decimal128(v); }

    @Transactional
    public PlaceBidResponse placeBid(String userId, PlaceBidRequest req) {
        PlaceBidResponse res = new PlaceBidResponse();

        // 0) Idempotency
        try {
            Idempotency idem = new Idempotency();
            idem.setUserId(userId);
            idem.setKey(req.getIdempotencyKey());
            idem.setSessionId(req.getSessionId());
            idem.setAmount(nvl(req.getAmount()));
            mongo.insert(idem);
        } catch (DuplicateKeyException ex) {
            AuctionSession s = mongo.findById(req.getSessionId(), AuctionSession.class);
            return rejectWithLeader(res,
                    "Duplicate idempotencyKey",
                    s != null ? nvl(s.getCurrentPrice()) : ZERO,
                    s != null ? s.getWinnerId() : null);
        }

        // 1) Đọc phiên & kiểm tra
        AuctionSession session = mongo.findById(req.getSessionId(), AuctionSession.class);
        if (session == null) return rejectWithLeader(res, "Session not found", ZERO, null);
        if (session.getStatus() != STATUS_LIVE)
            return rejectWithLeader(res, "Session is not LIVE", safe(session), session.getWinnerId());

        BigDecimal currentPrice = safe(session);
        BigDecimal bidStep      = nvl(session.getBidStep());
        BigDecimal amount       = nvl(req.getAmount());

        BigDecimal minAccept = currentPrice.add(bidStep);
        if (lt(amount, minAccept))
            return rejectWithLeader(res,
                    "Bid too low. Min = " + minAccept.toPlainString(),
                    currentPrice,
                    session.getWinnerId());

        // ❌ Chặn user đang là leader đặt tiếp
        if (Objects.equals(session.getWinnerId(), userId))
            return rejectWithLeader(res,
                    "You are already the current leader. Wait for another bid.",
                    currentPrice,
                    session.getWinnerId());

        // 2) Kiểm tra ví theo userId
        Wallet wallet = mongo.findOne(new Query(Criteria.where("userId").is(userId)), Wallet.class);
        if (wallet == null)
            return rejectWithLeader(res, "Wallet not found", currentPrice, session.getWinnerId());

        BigDecimal balance = nvl(wallet.getBalance());
        BigDecimal frozen  = nvl(wallet.getFrozenBalance());
        BigDecimal available = balance.subtract(frozen);
        if (lt(available, amount))
            return rejectWithLeader(res, "Insufficient balance", currentPrice, session.getWinnerId());

        // 3) Atomic nâng giá: winnerId != userId để chặn double-bid
        Query q = new Query(Criteria.where("_id").is(req.getSessionId())
                .and("status").is(STATUS_LIVE)
                .and("currentPrice").lt(amount)
                .and("winnerId").ne(userId));
        Update u = new Update()
                .set("currentPrice", amount)
                .set("winnerId", userId)
                .set("updatedAt", LocalDateTime.now());

        AuctionSession oldSession = mongo.findAndModify(q, u,
                FindAndModifyOptions.options().returnNew(false),
                AuctionSession.class);

        if (oldSession == null) {
            // Có người khác nhanh tay → trả leader hiện tại nhất
            AuctionSession latest = mongo.findById(req.getSessionId(), AuctionSession.class);
            res.setResult(BidResult.OUTBID);
            res.setCurrentPrice(latest != null ? nvl(latest.getCurrentPrice()) : currentPrice);
            res.setLeader(latest != null ? latest.getWinnerId() : session.getWinnerId()); // ✅ luôn set leader
            res.setMessage("Someone bid faster");
            saveIdemStatus(userId, req, res);
            return res;
        }

        BigDecimal oldPrice = nvl(oldSession.getCurrentPrice());
        String oldLeaderId  = oldSession.getWinnerId();

        // 4) Cập nhật frozen_balance theo userId
        if (oldLeaderId != null) {
            mongo.updateFirst(
                    new Query(Criteria.where("userId").is(oldLeaderId)),
                    new Update().inc("frozen_balance", d128(oldPrice.negate()))
                            .set("updatedAt", LocalDateTime.now()),
                    Wallet.class);
        }
        if (gt(amount, ZERO)) {
            mongo.updateFirst(
                    new Query(Criteria.where("userId").is(userId)),
                    new Update().inc("frozen_balance", d128(amount))
                            .set("updatedAt", LocalDateTime.now()),
                    Wallet.class);
        }

        // 5) Log bid
        Bids bid = new Bids();
        bid.setAuctionSessionId(req.getSessionId());
        bid.setUserId(userId);
        bid.setAmountAtThatTime(amount);
        bid.setBidTime(LocalDateTime.now());
        mongo.insert(bid);

        // 6) Success
        res.setResult(BidResult.BID_ACCEPTED);
        res.setCurrentPrice(amount);
        res.setLeader(userId); // ✅ leader luôn có
        res.setMessage("Accepted");

        Map<String, Object> payload = Map.of(
                "sessionId", req.getSessionId(),
                "price", amount,
                "leaderId", userId,
                "at", Instant.now().toString()
        );
        ws.convertAndSend("/topic/auction." + req.getSessionId() + ".bids", payload);

        saveIdemStatus(userId, req, res);
        return res;
    }

    /* ===================== helpers ===================== */

    private PlaceBidResponse rejectWithLeader(PlaceBidResponse res, String msg, BigDecimal currentPrice, String leaderId) {
        res.setResult(BidResult.BID_REJECTED);
        res.setMessage(msg);
        res.setCurrentPrice(nvl(currentPrice));
        res.setLeader(leaderId); // ✅ luôn set leader hiện tại (có thể null nếu chưa có ai dẫn)
        return res;
    }

    private void saveIdemStatus(String userId, PlaceBidRequest req, PlaceBidResponse res) {
        Query q = new Query(Criteria.where("userId").is(userId)
                .and("key").is(req.getIdempotencyKey()));
        Update u = new Update().set("status", res.getResult().name());
        mongo.updateFirst(q, u, Idempotency.class);
    }

    private static BigDecimal nvl(BigDecimal v) { return v == null ? ZERO : v; }
    private static boolean lt(BigDecimal a, BigDecimal b) { return a.compareTo(b) < 0; }
    private static boolean gt(BigDecimal a, BigDecimal b) { return a.compareTo(b) > 0; }

    private static BigDecimal safe(AuctionSession s) {
        BigDecimal cur = s.getCurrentPrice();
        if (cur != null) return cur;
        return s.getStartingPrice() != null ? s.getStartingPrice() : ZERO;
    }
}
