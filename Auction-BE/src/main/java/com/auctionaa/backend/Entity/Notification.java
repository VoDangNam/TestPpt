package com.auctionaa.backend.Entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "notifications")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notification extends BaseEntity {

    private String userId;
    private String notificationType;
    private String Title;
    private String notificationContent;
    private int notificationStatus;
    private LocalDateTime notificationTime;
    private String refId; // ID tham chiếu đối tượng cụ thể (id phiên, id đấu giá...)
    @CreatedDate
    private LocalDateTime createdAt;

    @Override
    public String getPrefix() {
        return "NOTI-";
    }
}
