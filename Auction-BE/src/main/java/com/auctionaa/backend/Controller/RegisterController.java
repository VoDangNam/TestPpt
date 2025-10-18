package com.auctionaa.backend.Controller;

import com.auctionaa.backend.DTO.Request.RegisterRequest;
import com.auctionaa.backend.DTO.Response.AuthResponse;
import com.auctionaa.backend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class RegisterController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public AuthResponse CTregister(@RequestBody RegisterRequest request) {
        return userService.register(request);
    }
}
