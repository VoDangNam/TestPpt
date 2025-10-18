package com.auctionaa.backend.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class StreamStartRequest {

    @NotBlank(message = "hostId is required")
    private String adminId;

    @NotBlank(message = "roomName is required")
    private String roomName;

    private String description;

    private String imageAuctionRoom;

    private String type; // optional, ví dụ "auction", "live"

}
