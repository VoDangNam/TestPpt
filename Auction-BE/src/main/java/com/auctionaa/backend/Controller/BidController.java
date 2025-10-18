package com.auctionaa.backend.Controller;

import com.auctionaa.backend.DTO.Request.PlaceBidRequest;
import com.auctionaa.backend.DTO.Response.PlaceBidResponse;
import com.auctionaa.backend.Entity.User;
import com.auctionaa.backend.Entity.Wallet;
import com.auctionaa.backend.Jwt.JwtUtil;
import com.auctionaa.backend.Repository.UserRepository;
import com.auctionaa.backend.Repository.WalletRepository;
import com.auctionaa.backend.Service.BidService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/bids")
@RequiredArgsConstructor
public class BidController {

    private final BidService bidService;
    private final JwtUtil jwtUtil;
    private final WalletRepository walletRepo;
    private final UserRepository userRepo;
    /**
     * Đặt giá realtime với idempotencyKey.
     * Header: Authorization: Bearer <JWT> (tuỳ bạn bật Security)
     */
    @PostMapping("/place")
    public ResponseEntity<PlaceBidResponse> place(@RequestBody PlaceBidRequest body,
                                                  @RequestHeader("Authorization") String authHeader) {
        String userId = jwtUtil.extractUserId(authHeader);
        Wallet wallet = walletRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ví với ID: " + userId));
        return ResponseEntity.ok(bidService.placeBid(userId, body));
    }
}
