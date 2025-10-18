package com.auctionaa.backend.Entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "artworks")
public class Artwork extends BaseEntity {

    // Lưu thẳng ownerId để query nhanh
    private String ownerId;

    private String title;
    private String description;
    private String avtArtwork;
    private List<String> imageUrls;
    private int status;
    private boolean aiVerified;
    private BigDecimal startedPrice;

    private String paintingGenre;// The loai

    private int yearOfCreation; // chỉ lưu năm
    private String material;
    private String size;

    private String certificateId; // thay vì @DBRef

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Override
    public String getPrefix() {
        return "Aw-";
    }
}
