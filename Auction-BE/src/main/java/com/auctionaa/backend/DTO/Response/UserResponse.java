package com.auctionaa.backend.DTO.Response;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserResponse {
    private String id;
    private String username;
    private String email;
    private String phonenumber;
    private int status;
    private String cccd;
    private String address;
    private String avt;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime dateOfBirth;
    private int gender; // 0 = male, 1 = female, 2 = other

}
