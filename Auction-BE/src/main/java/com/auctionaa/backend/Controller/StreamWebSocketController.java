package com.auctionaa.backend.Controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class StreamWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public StreamWebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/stream/{roomId}/signal")
    public void signaling(@DestinationVariable String roomId, @Payload Map<String, Object> payload) {
        messagingTemplate.convertAndSend("/topic/stream/" + roomId, payload);
    }
}
