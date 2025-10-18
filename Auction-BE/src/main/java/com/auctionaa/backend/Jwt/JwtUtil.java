package com.auctionaa.backend.Jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Cho phép dùng app.jwt.secret; (có thể đổi sang ${app.jwt.secret:${security.jwt.secret}} nếu muốn fallback)
    @Value("${app.jwt.secret}")
    private String secret;

    private Key key;

    private static final long EXPIRATION_TIME = 1000L * 60 * 60; // 1h

    @PostConstruct
    public void init() {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("JWT secret must be at least 32 characters for HS256");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** subject = userId */
    public String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** Lấy userId từ token hoặc từ header "Authorization: Bearer <token>" */
    public String extractUserId(String headerOrToken) {
        String token = sanitize(headerOrToken);
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /** Kiểm tra token hợp lệ (hết hạn/sai key -> false) */
    public boolean validateToken(String headerOrToken) {
        try {
            String token = sanitize(headerOrToken);
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /** Cắt "Bearer " nếu có */
    private String sanitize(String headerOrToken) {
        if (headerOrToken == null) return "";
        String t = headerOrToken.trim();
        if (t.startsWith("Bearer ")) t = t.substring(7);
        return t.trim();
    }

    // Nếu cần giữ API cũ để không vỡ code khác:
    /** @deprecated Token đang chứa userId, không phải email. Dùng extractUserId(). */
    @Deprecated
    public String extractEmail(String headerOrToken) {
        return extractUserId(headerOrToken);
    }
}
