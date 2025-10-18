package com.auctionaa.backend.DTO.Request;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateInvoiceRequest {
    private String auctionRoomId;
    private String userId;
    private String artworkId;

    private BigDecimal amount;
    private String paymentMethod;
    private int paymentStatus; // 0/1/2
    private int transactionType;

    private BigDecimal shippingFee;
    private BigDecimal totalAmount;
    private LocalDateTime paymentDate;
    private LocalDateTime orderDate;

    private String recipientUserId; // optional
    private String recipientNameText; // optional
    private String note; // optional
}
