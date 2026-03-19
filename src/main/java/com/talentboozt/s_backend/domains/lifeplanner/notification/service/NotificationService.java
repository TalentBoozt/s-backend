package com.talentboozt.s_backend.domains.lifeplanner.notification.service;

import org.springframework.stereotype.Service;
import com.talentboozt.s_backend.domains.lifeplanner.notification.model.Notification;
import com.talentboozt.s_backend.domains.lifeplanner.notification.repository.mongodb.NotificationRepository;
import lombok.RequiredArgsConstructor;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public Notification createNotification(String userId, String title, String message, String type) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);
        notification.setCreatedAt(Instant.now());
        return notificationRepository.save(notification);
    }

    public List<Notification> getUserNotifications(String userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public int getUnreadCount(String userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    public void markAsRead(String id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    public void deleteNotification(String id) {
        notificationRepository.deleteById(id);
    }
}
