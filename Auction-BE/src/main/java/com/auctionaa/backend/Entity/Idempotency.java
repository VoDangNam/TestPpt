package com.auctionaa.backend.Entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Document("idempotency")
@CompoundIndex(def = "{'userId':1,'key':1}", unique = true)
public class Idempotency {
    @Id
    private String id;

    private String userId;
    private String key;
    private String sessionId;
    private BigDecimal amount;       // số tiền bid trong request đó
    private String status;           // "BID_ACCEPTED" | "OUTBID" | "BID_REJECTED"
    private Instant createdAt = Instant.now();
}