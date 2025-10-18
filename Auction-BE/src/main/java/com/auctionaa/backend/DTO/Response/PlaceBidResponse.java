package com.auctionaa.backend.DTO.Response;

import com.auctionaa.backend.DTO.Result.BidResult;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PlaceBidResponse {
    private BidResult result;
    private BigDecimal currentPrice;
    private String leader;
    private String message;
}
