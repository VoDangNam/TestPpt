package com.auctionaa.backend.DTO.Response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// com.auctionaa.backend.DTO.Response.AuctionRoomLiveDTO
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class AuctionRoomLiveDTO {
    private String id;                 // roomId
    private String roomName;
    private String imageAuctionRoom;
    private String type;
    private int status;                // status của room
    private List<String> memberIds;
    private String sessionId;          // phiên đang diễn ra (nếu có)
    private LocalDateTime startTime;   // phiên
    private LocalDateTime endTime;     // phiên
    private BigDecimal startingPrice;  // phiên
    private BigDecimal currentPrice;   // phiên (đang chạy)
}
