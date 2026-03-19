package com.talentboozt.s_backend.domains.lifeplanner.notification.service;

import org.springframework.stereotype.Service;
import com.talentboozt.s_backend.domains.lifeplanner.notification.model.LPNotification;
import com.talentboozt.s_backend.domains.lifeplanner.notification.repository.mongodb.LPNotificationRepository;
import lombok.RequiredArgsConstructor;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LPNotificationService {
    private final LPNotificationRepository notificationRepository;

    public LPNotification createNotification(String userId, String title, String message, String type) {
        LPNotification notification = new LPNotification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);
        notification.setCreatedAt(Instant.now());
        return notificationRepository.save(notification);
    }

    public List<LPNotification> getUserNotifications(String userId) {
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
