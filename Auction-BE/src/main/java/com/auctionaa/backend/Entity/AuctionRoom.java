package com.auctionaa.backend.Entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "auction_rooms")
public class AuctionRoom extends BaseEntity {

    private String adminId; // thay DBRef
    private List<String> memberIds; // thay DBRef
    private Integer viewCount; // so_luot_xem
    private String roomName;
    private String description;
    private String imageAuctionRoom;
    private String type;
    private int status;

    private LocalDateTime startedAt;
    private LocalDateTime stoppedAt;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Override
    public String getPrefix() {
        return "ACR-";
    }
}
