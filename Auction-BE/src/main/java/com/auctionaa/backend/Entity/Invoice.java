package com.auctionaa.backend.Entity;

import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "invoices")
public class Invoice extends BaseEntity {

    @Indexed
    private String auctionRoomId; // "ACR-1"
    @Indexed
    private String userId; // "U-..."
    @Indexed
    private String artworkId; // "Aw-..."

    // Snapshot để list hiển thị nhanh
    private String artworkTitle;
    private String artistName; // tên artist (từ Artwork)
    private String roomName;

    // Giao dịch
    private BigDecimal amount;
    private String paymentMethod;
    @Indexed
    private int paymentStatus; // 0=pending, 1=paid, 2=failed
    private int transactionType;

    private BigDecimal shippingFee;
    private BigDecimal totalAmount;

    @Indexed
    private LocalDateTime paymentDate;
    @Indexed
    private LocalDateTime orderDate;

    // Người nhận
    private String recipientNameText;
    private String note;

    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Override
    public String getPrefix() {
        return "IV-";
    }
}
