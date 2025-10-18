package com.auctionaa.backend.Controller;

import com.auctionaa.backend.DTO.Request.LoginRequest;
import com.auctionaa.backend.DTO.Request.TokenResponse;
import com.auctionaa.backend.DTO.Response.AuthResponse;
import com.auctionaa.backend.Entity.User;
import com.auctionaa.backend.Jwt.JwtUtil;
import com.auctionaa.backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // API login
    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new AuthResponse(0, "User not found"));
        }

        User user = userOptional.get();

        if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            String token = jwtUtil.generateToken(user.getId());
            user.setStatus(1); // ✅ đánh dấu user đang active
            userRepository.save(user);
            /* AuthResponse response = new AuthResponse(1, "Login successfully", token); */
            AuthResponse response = new AuthResponse(user.getStatus(), "Login successfully", token);

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(new AuthResponse(0, "Invalid password"));
        }
    }

    // API lấy email + username từ token
    @GetMapping("/getEmailAndUsernameFromToken")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String id = jwtUtil.extractUserId(token);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tạo response DTO
        TokenResponse tokenResponse = new TokenResponse(user.getUsername(), user.getEmail());

        return ResponseEntity.ok(tokenResponse);
    }
}
