package com.auctionaa.backend.Entity;

import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "wallet_transactions")
public class WalletTransaction extends BaseEntity {

    private String walletId;

    @Field("id_User")
    private String userId;

    @Field("id_AuctionSession")
    private String auctionSessionId;

    @DecimalMin(value = "0.0", inclusive = true, message = "Số tiền giao dịch không được âm.")
    @Field("balance")
    private BigDecimal balance;

    @Field("created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Field("updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Field("status")
    private int status;

    @Field("note")
    private String note;

    @Field("transactionType")
    private int transactionType;

    @Override
    public String getPrefix() {
        return "WLT-";
    }
}