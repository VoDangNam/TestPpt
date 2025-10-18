package com.auctionaa.backend.Entity;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "wallets")
public class Wallet extends BaseEntity {
    @NotBlank
    @Indexed(unique = true)                       // 1 user ↔ 1 ví
    private String userId;

    @DecimalMin(value = "0.0", inclusive = true, message = "Số dư không được âm.")
    @Field(value = "balance", targetType = FieldType.DECIMAL128)
    private BigDecimal balance;                   // tổng nạp

    @DecimalMin(value = "0.0", inclusive = true, message = "Số dư khóa không được âm.")
    @Field(value = "frozen_balance", targetType = FieldType.DECIMAL128)
    private BigDecimal frozenBalance;             // số dư đang giữ (hold) cho các phiên

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Override
    public String getPrefix() { return "WL-"; }

    // --- Webhook fields ---
    @Field("callback_url")
    private String callbackUrl;

    @Field("webhook_secret")
    private String webhookSecret; // HMAC secret cho bên nhận verify

    @Field("webhook_status")
    private String webhookStatus; // PENDING | SENT | FAILED

    @Field("webhook_attempts")
    private int webhookAttempts;

    @Field("last_webhook_at")
    private LocalDateTime lastWebhookAt;

    @Field("bank_txn_id")
    private String bankTxnId; // Id giao dịch từ MB khi đối soát thành công

    @Transient
    public BigDecimal getAvailable() {
        BigDecimal bal = balance == null ? BigDecimal.ZERO : balance;
        BigDecimal frz = frozenBalance == null ? BigDecimal.ZERO : frozenBalance;
        return bal.subtract(frz);
    }
}
