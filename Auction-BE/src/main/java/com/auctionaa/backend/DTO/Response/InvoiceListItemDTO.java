package com.auctionaa.backend.DTO.Response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InvoiceListItemDTO(
                String id,
                String auctionRoomId,
                String artworkId,
                String artworkTitle,
                String artworkAvt,
                BigDecimal totalAmount,
                Integer paymentStatus,
                LocalDateTime createdAt) {
}
