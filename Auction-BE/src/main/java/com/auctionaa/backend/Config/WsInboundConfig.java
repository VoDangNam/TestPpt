package com.auctionaa.backend.Config;

import com.auctionaa.backend.WebSocket.StompAuthChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
public class WsInboundConfig implements WebSocketMessageBrokerConfigurer {
    private final StompAuthChannelInterceptor interceptor;

    @Override public void configureClientInboundChannel(ChannelRegistration reg) {
        reg.interceptors(interceptor);
    }
}
