package com.auctionaa.backend.Service;

import com.auctionaa.backend.DTO.Response.VerifyTopUpResponse;
import com.auctionaa.backend.Entity.MbTxn;
import com.auctionaa.backend.Entity.Wallet;
import com.auctionaa.backend.Entity.WalletTransaction;
import com.auctionaa.backend.Repository.WalletRepository;
import com.auctionaa.backend.Repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VerifyCaptureService {

    private final WalletTransactionRepository txnRepo;
    private final WalletRepository walletRepo;
    private final MbClient mbClient;
    private final WebhookService webhookService;

    /**
     * Kiểm tra lsgd MB xem có giao dịch khớp với transaction (note + amount) không.
     * Nếu có: set status=1, cộng số dư, set bank_txn_id, bắn webhook.
     */
    @Transactional
    public VerifyTopUpResponse verifyAndCapture(String id) {
        WalletTransaction txn = txnRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction không tồn tại"));

        Wallet wallet = walletRepo.findById(txn.getWalletId())
                .orElseThrow(() -> new IllegalStateException("Wallet không tồn tại"));

        // Idempotent
        if (txn.getStatus() == 1) {
            return new VerifyTopUpResponse(txn.getId(), 1, wallet.getBankTxnId(), "Đã nạp trước đó");
        }

        // Khoảng thời gian tìm trên MB (nới rộng để chắc chắn)
        LocalDate from = (txn.getCreatedAt() != null ? txn.getCreatedAt().toLocalDate() : LocalDate.now()).minusDays(1);
        LocalDate to   = LocalDate.now().plusDays(1);

        List<MbTxn> list = mbClient.fetchRecentTransactions(from, to);

        // Chuẩn hóa điều kiện khớp
        String note = safe(txn.getNote());                  // "TOPUP_68efaa1e3f0493f461d27d98_1760626976085"
        String noteAlt = note.replace("_", " ");            // phòng trường hợp addInfo hiển thị thành khoảng trắng
        BigDecimal mustAmount = new BigDecimal(String.valueOf(txn.getBalance())); // "2000000" -> 2000000

        MbTxn matched = list.stream()
                .filter(t -> parseBig(t.getCreditAmount()).compareTo(BigDecimal.ZERO) > 0) // chỉ nhận tiền vào
                .filter(t -> {
                    BigDecimal credit = parseBig(t.getCreditAmount());
                    return credit.compareTo(mustAmount) == 0;
                })
                .filter(t -> {
                    String d1 = safe(t.getDescription());
                    String d2 = safe(t.getAddDescription());
                    return containsLoose(d1, note) || containsLoose(d1, noteAlt)
                            || containsLoose(d2, note) || containsLoose(d2, noteAlt);
                })
                .findFirst()
                .orElse(null);

        if (matched == null) {
            return new VerifyTopUpResponse(txn.getId(), 0, null, "Chưa tìm thấy giao dịch khớp trong lsgd MB");
        }

        // --- Cập nhật trạng thái & số dư ---
        txn.setStatus(1);
        txnRepo.save(txn);

        // cộng ví
        wallet.setBalance(wallet.getBalance().add(mustAmount));
        // lưu bank_txn_id để truy vết
        wallet.setBankTxnId(matched.getRefNo());
        walletRepo.save(wallet);

        // webhook (fire & forget)
        webhookService.sendTopupSucceeded(wallet, txn);

        return new VerifyTopUpResponse(txn.getId(), 1, matched.getRefNo(), "Nạp thành công");
    }

    private static BigDecimal parseBig(String s) {
        try { return new BigDecimal(s.replace(",", "").trim()); }
        catch (Exception e) { return BigDecimal.ZERO; }
    }

    private static String safe(String s) { return s == null ? "" : s; }

    // so khớp "lỏng": bỏ dấu, bỏ khoảng trắng thừa, lowercase
    private static boolean containsLoose(String haystack, String needle) {
        String h = loosen(haystack);
        String n = loosen(needle);
        return !n.isEmpty() && h.contains(n);
    }

    private static String loosen(String s) {
        String n = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", ""); // bỏ dấu
        n = n.replaceAll("[_\\s\\t\\n\\r]+", ""); // ⚡ bỏ luôn "_" và khoảng trắng
        return n.trim().toLowerCase();
    }
}