package com.auctionaa.backend.Controller;

import com.auctionaa.backend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/info")
    public ResponseEntity<?> getInfoUser(@RequestHeader("Authorization") String authHeader) {
        return userService.getUserInfo(authHeader);
    }
}
