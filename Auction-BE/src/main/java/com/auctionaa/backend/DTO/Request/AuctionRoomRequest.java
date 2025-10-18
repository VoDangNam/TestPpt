package com.auctionaa.backend.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuctionRoomRequest {
    private String roomName;
    private String description;
    private int numberOfArtwork;
    private String type;
    private String imageAuctionRoom;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal startingPrice;
    private int status;
    private List<String> memberIds;
}
