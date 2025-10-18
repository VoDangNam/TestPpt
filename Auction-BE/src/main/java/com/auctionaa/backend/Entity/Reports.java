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
@Document(collection = "reports")
public class Reports extends BaseEntity {

    private String userId;
    private String objectId; // id đối tượng bị báo cáo
    private String object; // đóio tượng
    private String reportReason; // lis do baos caos
    private int reportStatus;

    @CreatedDate
    private LocalDateTime createdAt;
    private LocalDateTime reportDoneTime;

    @Override
    public String getPrefix() {
        return "RP-";
    }
}
