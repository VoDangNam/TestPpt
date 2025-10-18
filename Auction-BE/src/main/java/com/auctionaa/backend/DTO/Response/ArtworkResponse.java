package com.auctionaa.backend.DTO.Response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArtworkResponse {
    private String id;
    private String title;
    private String description;
    private String avtArtwork;
    private List<String> imageUrls;
    private int status;
    private boolean aiVerified;
    private BigDecimal startedPrice;
    private String paintingGenre;
    private int yearOfCreation;
    private String material;
    private String size;
    private String certificateId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String ownerName; // thêm
    private BigDecimal myLatestBidAmount;
}