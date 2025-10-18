package com.auctionaa.backend.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class StreamStartResponse {
    private String roomId;
    private String wsUrl;
    private int status;
}