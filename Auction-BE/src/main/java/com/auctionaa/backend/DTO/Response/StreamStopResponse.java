package com.auctionaa.backend.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class StreamStopResponse {
    private String message;
    private String status;
}
