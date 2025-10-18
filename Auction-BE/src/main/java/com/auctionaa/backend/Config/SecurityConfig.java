package com.auctionaa.backend.Config;

import com.auctionaa.backend.Jwt.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // JWT filter của bạn (nếu chưa có bean, tạo @Component cho JwtAuthFilter)
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // REST + JWT: tắt CSRF toàn cục (đơn giản, dễ test Postman)
                .csrf(csrf -> csrf.disable())
                // CORS cho FE (localhost:5173)
                .cors(Customizer.withDefaults())
                // JWT => stateless; tắt formLogin/httpBasic
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(f -> f.disable())
                .httpBasic(b -> b.disable())

                // Phân quyền
                .authorizeHttpRequests(auth -> auth
                        // WebSocket/SockJS
                        .requestMatchers("/ws/**", "/stomp/**").permitAll()

                        // Auth (public)
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()

                        // Stream (theo cấu hình bạn đang test)
                        // - startStream: bạn tự validate token trong controller => permitAll để vào controller
                        .requestMatchers(HttpMethod.POST, "/api/stream/**").permitAll()
                        // - getRoom public để người xem lấy thông tin phòng
                        .requestMatchers(HttpMethod.GET,  "/api/stream/room/**").permitAll()

                        // Ví dụ endpoint topup mở public (giữ nguyên từ config cũ)
                        .requestMatchers(HttpMethod.POST, "/api/wallets/topups").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/auctionroom/allAuctionRoom").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/wallets/{id}/verify-capture").permitAll()

                        // Cho phép /error để tránh 404 -> 403
                        .requestMatchers("/error").permitAll()

                        // Preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Còn lại: yêu cầu đã đăng nhập (JWT)
                        .anyRequest().authenticated()
                )

                // Đưa JWT filter vào trước UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CORS cho FE (Vite: http://localhost:5173)
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        config.setAllowedHeaders(List.of("*")); // hoặc liệt kê: Authorization, Content-Type, ...
        config.setAllowCredentials(false);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
