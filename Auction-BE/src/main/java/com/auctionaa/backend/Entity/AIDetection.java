package com.auctionaa.backend.Entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "ai_detections")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AIDetection extends BaseEntity {

    private String artworkId;

    private String aiDetectionResult;
    private LocalDateTime aiDetectionCreatedAt;
    private LocalDateTime aiDetectionUpdatedAt;

    @Override
    public String getPrefix() {
        return "AID-";
    }
}
