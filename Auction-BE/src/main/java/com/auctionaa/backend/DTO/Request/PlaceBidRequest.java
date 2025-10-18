package com.auctionaa.backend.DTO.Request;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class PlaceBidRequest {
    private String sessionId;
    private BigDecimal amount;
    private String idempotencyKey;
}
