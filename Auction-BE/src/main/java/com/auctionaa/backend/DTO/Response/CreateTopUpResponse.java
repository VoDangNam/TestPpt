package com.auctionaa.backend.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateTopUpResponse {
    private String transactionId;
    private String qrUrl;       // link ảnh QR compact2 (VietQR)
    private String qrImageUrl;  // alias cho dễ hiểu (giống qrUrl)
    private String noteUsed;    // nội dung chuyển khoản (để user thấy & đối soát)
}