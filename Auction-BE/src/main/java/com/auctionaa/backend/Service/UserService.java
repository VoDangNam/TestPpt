package com.auctionaa.backend.Service;

import com.auctionaa.backend.DTO.Request.RegisterRequest;
import com.auctionaa.backend.DTO.Response.AuthResponse;
import com.auctionaa.backend.DTO.Response.UserResponse;
import com.auctionaa.backend.Entity.User;
import com.auctionaa.backend.Jwt.JwtUtil;
import com.auctionaa.backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;

    // private RegisterResponse registerResponse;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthResponse register(RegisterRequest request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return new AuthResponse(0, "Incorrect password bro!!!");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return new AuthResponse(0, "Email already existed!!!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);
        user.setPhonenumber(request.getPhone());

        user.setCreatedAt(LocalDateTime.now());
        user.setStatus(1);
        // ✅ generate ID trước khi save
        user.generateId();

        userRepository.save(user);
        return new AuthResponse(1, "Register Successfully");

    }

    public Optional<User> login(String email, String rawPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public ResponseEntity<?> getUserInfo(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractUserId(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tạo response DTO
        UserResponse userResponse = new UserResponse(user.getId(), user.getUsername(), user.getEmail(),
                user.getPhonenumber(), user.getStatus(), user.getCccd(), user.getAddress(), user.getAvt(),
                user.getCreatedAt(), user.getUpdatedAt(), user.getDateOfBirth(), user.getGender());
        return ResponseEntity.ok(userResponse);
    }

}
