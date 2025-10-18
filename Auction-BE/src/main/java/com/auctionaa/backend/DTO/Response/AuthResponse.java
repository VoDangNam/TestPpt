package com.auctionaa.backend.DTO.Response;

import lombok.Data;

@Data

public class AuthResponse {
    private int status;
    private String message;
    private String token;

    public AuthResponse(int status, String message, String token) {
        this.message = message;
        this.status = status;
        this.token = token;
    }

    public AuthResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

}
