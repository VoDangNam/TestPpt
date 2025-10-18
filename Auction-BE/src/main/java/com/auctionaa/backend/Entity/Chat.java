package com.auctionaa.backend.Entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "chats")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Chat extends BaseEntity {

    private String content;
    private LocalDateTime deliveredTime;

    private String senderId;

    private String receiverId;

    private String aucttionId;

    @Override
    public String getPrefix() {
        return "Chat-";
    }
}
