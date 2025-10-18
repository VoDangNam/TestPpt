package com.auctionaa.backend.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "report_objects")
public class ReportObject extends BaseEntity {

    private String userId;
    private String auctionRoomId;
    private String artworkId;
    @CreatedDate
    private LocalDateTime createdAt;

    @Override
    public String getPrefix() {
        return "RpO-";
    }
}
