package com.auctionaa.backend.Controller;

import com.auctionaa.backend.Entity.Notification;
import com.auctionaa.backend.Jwt.JwtUtil;
import com.auctionaa.backend.Repository.NotificationRepository;
import com.auctionaa.backend.Service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notification")
@CrossOrigin(origins = "http://localhost:5173")
public class NotificationController {
    private NotificationService notificationService;
    private NotificationRepository notificationRepository;
    @Autowired
    private JwtUtil jwtUtil;

    /*
     * public NotificationController(NotificationService notificationService) {
     * this.notificationService = notificationService;
     * }
     */

    public NotificationController(NotificationService notificationService,
            NotificationRepository notificationRepository) {
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
    }

    // @GetMapping("/{id}")
    // public List<Notification> getNotificationById(@PathVariable String id){
    // return notificationService.getNotificationBy_OwnerId(id);
    // }

    @GetMapping("/my-notification")
    public List<Notification> getMyNotifications(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractUserId(token);
        return notificationService.getNotificationBy_OwnerId(email);
    }

    @PostMapping("/addNotification")
    public Notification creatNotification(@RequestBody Notification notification) {
        return notificationService.addNotification(notification);
    }

    @GetMapping("/getall")
    public List<Notification> getAll() {
        return notificationRepository.findAll();
    }
    // http://localhost:8081/api/notification/68c9154508a3858c703ad81f
    // http://localhost:8081/api/notification/addNotification
}