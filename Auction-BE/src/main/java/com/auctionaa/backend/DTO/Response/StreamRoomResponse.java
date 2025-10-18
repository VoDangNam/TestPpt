package com.auctionaa.backend.DTO.Response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class StreamRoomResponse {
    private String roomId;
    private String status;
    private String wsUrl;
    private Integer viewCount;
    private String roomName;

    private String description;

    private String imageAuctionRoom;

    private String type; // optional, ví dụ "auction", "live"

    private List<String> memberIds;
}