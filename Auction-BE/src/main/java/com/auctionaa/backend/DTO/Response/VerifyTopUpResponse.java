package com.auctionaa.backend.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyTopUpResponse {
    private String transactionId;
    private int status;            // 1 = SUCCESS, 0 = PENDING, -1 = FAILED
    private String bankRefNo;
    private String message;
}