package com.auctionaa.backend.Controller;

import com.auctionaa.backend.Config.ZegoCloudConfig;
import com.auctionaa.backend.DTO.Request.StreamStartRequest;
import com.auctionaa.backend.DTO.Response.StreamStartResponse;
import com.auctionaa.backend.Entity.AuctionRoom;
import com.auctionaa.backend.Entity.User;
import com.auctionaa.backend.Jwt.JwtUtil;
import com.auctionaa.backend.Repository.AuctionRoomRepository;
import com.auctionaa.backend.Repository.UserRepository;
import com.auctionaa.backend.Service.CloudinaryService;
import com.auctionaa.backend.Service.StreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/stream")
@RequiredArgsConstructor
public class StreamController {

    private final ZegoCloudConfig zegoConfig;
    private final StreamService streamService;
    private final CloudinaryService cloudinaryService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final AuctionRoomRepository roomRepo;
    @Value("${zegocloud.server-secret}")
    private String serverSecret;
    @PostMapping(value = "/start", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public StreamStartResponse startStream(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam String roomName,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) MultipartFile file
    ) throws IOException {

        // 1) Lấy & validate token
        String token = extractBearer(authHeader);
        if (!jwtUtil.validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        // 2) Lấy userId từ token -> tìm user
        String userId = jwtUtil.extractUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        // 3) Build request cho service
        StreamStartRequest rq = new StreamStartRequest();
        rq.setAdminId(user.getId());                 // <<< tự gắn adminId
        rq.setRoomName(roomName);
        rq.setDescription(description);
        rq.setType(type == null ? "public" : type);

        // 4) Tạo phòng
        AuctionRoom room = streamService.startStream(rq, file);

        // 5) Trả kết quả (dev: ws:// ; prod: wss://)
        return StreamStartResponse.builder()
                .roomId(room.getId())
                .wsUrl("ws://localhost:8081/ws/stream/" + room.getId())
                .status(room.getStatus())
                .build();
    }

    @GetMapping("/room/{roomId}")
    public AuctionRoom getRoom(@PathVariable String roomId){
        return streamService.getRoom(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
    }

    @PostMapping("/stop/{roomId}")
    public StreamStartResponse stopStream(@PathVariable String roomId) {
        streamService.stopStream(roomId);
        return StreamStartResponse.builder()
                .roomId(roomId)
                .status(0)
                .wsUrl(null)
                .build();
    }

    @PostMapping("/token")
    public Map<String, Object> issueToken(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String roomId
    ) {
        String userId = jwtUtil.extractUserId(authHeader);

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        var room = roomRepo.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));

        String role = userId.equals(room.getAdminId()) ? "host" : "audience";

        long now = System.currentTimeMillis() / 1000;
        long expireAt = now + zegoConfig.getTokenTtl();


        return Map.of(
                "appID", zegoConfig.getAppId(),
                "token", serverSecret,
                "userId", userId,
                "roomId", roomId,
                "role", role,
                "expireAt", expireAt
        );
    }

    // --- helpers ---
    private String extractBearer(String header) {
        if (header == null || !header.startsWith("Bearer "))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing Bearer token");
        return header.substring(7).trim();
    }
}
