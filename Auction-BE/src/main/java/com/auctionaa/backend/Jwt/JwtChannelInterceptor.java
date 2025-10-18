package com.auctionaa.backend.Jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Collections;
import java.util.List;

@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    @Value("${security.jwt.secret}")
    private String secret;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor acc = StompHeaderAccessor.wrap(message);

        // ✅ Khi client CONNECT
        if (acc.getCommand() == StompCommand.CONNECT) {
            String auth = firstHeader(acc, "Authorization");
            if (auth == null) auth = firstHeader(acc, "authorization");

            System.out.println("[STOMP CONNECT] rawAuth=" + auth);

            Principal principal;

            if (auth != null) {
                String v = auth.trim();
                if (!v.toLowerCase().startsWith("bearer ")) {
                    v = "Bearer " + v;
                }
                String token = v.substring("Bearer ".length()).trim();
                try {
                    Claims claims = Jwts.parserBuilder()
                            .setSigningKey(key())
                            .build()
                            .parseClaimsJws(token)
                            .getBody();

                    String userId = claims.getSubject();
                    principal = new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            Collections.emptyList()
                    );
                    System.out.println("[STOMP CONNECT] principal=" + userId);
                } catch (Exception e) {
                    System.out.println("[JWT PARSE ERROR] " + e.getMessage());
                    principal = () -> "anonymous";
                }
            } else {
                principal = () -> "anonymous";
            }

            acc.setUser(principal);
            if (acc.getSessionAttributes() != null) {
                acc.getSessionAttributes().put("SPRING.PRINCIPAL", principal);
            }
        }
        // ✅ Với các message SEND, SUBSCRIBE, DISCONNECT, giữ lại principal
        else if (acc.getUser() == null && acc.getSessionAttributes() != null) {
            Principal sessionUser = (Principal) acc.getSessionAttributes().get("SPRING.PRINCIPAL");
            if (sessionUser != null) {
                acc.setUser(sessionUser);
            }
        }

        // ✅ Debug log nhỏ
        if (acc.getCommand() == StompCommand.SEND) {
            System.out.println("[STOMP SEND] from user=" +
                    (acc.getUser() != null ? acc.getUser().getName() : "null"));
        }

        return message;
    }

    private String firstHeader(StompHeaderAccessor acc, String name) {
        List<String> vals = acc.getNativeHeader(name);
        return (vals != null && !vals.isEmpty()) ? vals.get(0) : null;
    }
}
