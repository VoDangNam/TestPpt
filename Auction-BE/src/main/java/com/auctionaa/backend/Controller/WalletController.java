package com.auctionaa.backend.Controller;

import com.auctionaa.backend.DTO.Request.CreateTopUpRequest;
import com.auctionaa.backend.DTO.Response.CreateTopUpResponse;
import com.auctionaa.backend.DTO.Response.VerifyTopUpResponse;
import com.auctionaa.backend.Entity.Wallet;
import com.auctionaa.backend.Entity.WalletTransaction;
import com.auctionaa.backend.Jwt.JwtUtil;
import com.auctionaa.backend.Repository.WalletRepository;
import com.auctionaa.backend.Repository.WalletTransactionRepository;
import com.auctionaa.backend.Service.TopUpService;
import com.auctionaa.backend.Service.VerifyCaptureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {
    private final TopUpService topUpService;
    private final VerifyCaptureService verifyCaptureService;
    private final WalletTransactionRepository txnRepo;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private WalletRepository walletRepo;

    @PostMapping("/topups")
    public ResponseEntity<CreateTopUpResponse> create(
            @Valid @RequestBody CreateTopUpRequest req,
            @RequestHeader("Authorization") String authHeader) {

        String userId = jwtUtil.extractUserId(authHeader); // subject = userId

        Wallet wallet = walletRepo.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet không tồn tại"));

        return ResponseEntity.ok(topUpService.createTopUp(wallet.getId(), req));
    }


//    @PostMapping("/{transactionId}/verify")
//    public ResponseEntity<VerifyTopUpResponse> verifyAndCapture(@PathVariable String transactionId, @RequestHeader("Authorization") String authHeader) {
//        String token = authHeader.replace("Bearer ", "").trim();
//        String email = jwtUtil.extractUserId(token);
//        return ResponseEntity.ok(verifyCaptureService.verifyAndCapture(transactionId));
//    }

    @PostMapping("/{id}/verify-capture")
    public VerifyTopUpResponse verifyAndCapture(@PathVariable String id) {
        return verifyCaptureService.verifyAndCapture(id);
    }

    @GetMapping("/getWallet")
    public ResponseEntity<?> getWallet( @RequestHeader("Authorization") String authHeader) {
        String userId = jwtUtil.extractUserId(authHeader);
        Wallet wallet = walletRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ví với ID: " + userId));
        return ResponseEntity.ok(wallet);
    }

    @GetMapping("/transactionHistories")
    public List<WalletTransaction> Histories(@RequestHeader("Authorization") String authHeader,
                                             @RequestParam(defaultValue = "1") int status )
    {
        // 1️⃣ Giải mã userId trực tiếp từ JWT (JwtUtil tự xử lý "Bearer ")
        String userId = jwtUtil.extractUserId(authHeader);

        // 2️⃣ Lấy ví của user
        Wallet wallet = walletRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ví của user: " + userId));

        // 3️⃣ Lấy danh sách transaction theo walletId + status
        List<WalletTransaction> txns = txnRepo.findByWalletIdAndStatus(wallet.getId(), status);

        // 4️⃣ Ghi log kiểm tra (tùy chọn)
        System.out.printf("[DEBUG] userId=%s | walletId=%s | status=%d | count=%d%n",
                userId, wallet.getId(), status, txns.size());

        return txns;
    }
}