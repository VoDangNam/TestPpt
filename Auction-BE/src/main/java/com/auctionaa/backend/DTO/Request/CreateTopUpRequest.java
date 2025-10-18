package com.auctionaa.backend.DTO.Request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateTopUpRequest {
//    @NotBlank
//    private String walletId;
    @NotNull
    @DecimalMin("1000.0")
    private BigDecimal amount;
    private String note; // sẽ nhúng vào VietQR addInfo (nên kèm mã WLT-...)
}
