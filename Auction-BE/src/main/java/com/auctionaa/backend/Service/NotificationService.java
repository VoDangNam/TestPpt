package com.auctionaa.backend.Service;

import com.auctionaa.backend.Entity.Notification;
import com.auctionaa.backend.Entity.User;
import com.auctionaa.backend.Repository.NotificationRepository;
import com.auctionaa.backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> getNotificationBy_OwnerId(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.findByUserId(user.getId());

    }

    public Notification addNotification(Notification notification) {
        if (notification.getId() == null) {
            notification.generateId();

        }
        if (notification.getCreatedAt() == null) {
            notification.setCreatedAt(LocalDateTime.now());
        }

        notification.setNotificationTime(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

}
