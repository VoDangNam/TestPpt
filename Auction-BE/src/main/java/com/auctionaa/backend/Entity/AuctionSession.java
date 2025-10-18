package com.auctionaa.backend.Entity;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "auction_sessions")
@CompoundIndexes({
        @CompoundIndex(name = "idx_room_status", def = "{'auctionRoomId': 1, 'status': 1}"),
        @CompoundIndex(name = "idx_time",       def = "{'startTime': 1, 'endTime': 1}"),
        @CompoundIndex(name = "idx_status_price", def = "{'status': 1, 'current_price': 1}")
})
public class AuctionSession extends BaseEntity {

    @Indexed
    private String auctionRoomId;

    @Indexed
    private String artworkId;                    // FK tới tác phẩm

    private String imageUrl;

    @Indexed
    private LocalDateTime startTime;

    @Indexed
    private LocalDateTime endTime;

    @DecimalMin(value = "0.0", inclusive = true)
    @Field(value = "starting_price", targetType = FieldType.DECIMAL128)
    private BigDecimal startingPrice;

    @DecimalMin(value = "0.0", inclusive = true)
    @Field(value = "current_price", targetType = FieldType.DECIMAL128)
    private BigDecimal currentPrice;

    /**
     * Trạng thái nên dùng enum ở tầng service.
     * 0=DRAFT, 1=SCHEDULED, 2=LIVE, 3=CLOSED, 4=CANCELLED, ...
     */
    @Indexed
    private int status;

    /** người đang dẫn hiện tại (email/userId); khi CLOSED có thể copy sang winnerId/finalPrice */
    @Indexed
    private String winnerId;

    private String type;

    @Indexed
    private Integer viewCount;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Bước giá phải > 0.")
    @Field(value = "bid_step", targetType = FieldType.DECIMAL128)
    private BigDecimal bidStep;

    @Field(value = "final_price", targetType = FieldType.DECIMAL128)
    private BigDecimal finalPrice;

    private Integer bidCount;

    @Indexed
    private String sellerId;

    @Override
    public String getPrefix() { return "ATSS-"; }

    /** đảm bảo currentPrice luôn có giá trị hợp lệ để so sánh */
    @Transient
    public BigDecimal safeCurrentPrice() {
        if (currentPrice != null) return currentPrice;
        return startingPrice == null ? BigDecimal.ZERO : startingPrice;
    }
}
