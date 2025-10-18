package com.auctionaa.backend.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "ai_classifications")
public class AIClassification extends BaseEntity {

    private LocalDateTime createdAt;

    private String artworkId;

    private String genreName;

    @Override
    public String getPrefix() {
        return "AIC-";
    }
}
