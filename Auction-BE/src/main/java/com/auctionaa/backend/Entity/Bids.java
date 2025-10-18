package com.auctionaa.backend.Entity;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "bids")
@CompoundIndexes({
        @CompoundIndex(name = "idx_session_time", def = "{'auctionSessionId': 1, 'bidTime': 1}"),
        @CompoundIndex(name = "idx_session_user", def = "{'auctionSessionId': 1, 'userId': 1}")
})
public class Bids extends BaseEntity {

    @Indexed
    private LocalDateTime bidTime;

    @NotBlank
    @Indexed
    private String auctionSessionId;

    @NotBlank
    @Indexed
    private String userId;

    /** Giá tại thời điểm đặt (snapshot) */
    @DecimalMin(value = "0.0", inclusive = true)
    @Field(value = "amount_at_that_time", targetType = FieldType.DECIMAL128)
    private BigDecimal amountAtThatTime;

    @CreatedDate
    private LocalDateTime createdAt;

    @Override
    public String getPrefix() { return "Bid-"; }
}
