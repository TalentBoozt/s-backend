package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.enums.ENotificationType;
import com.talentboozt.s_backend.domains.edu.model.ENotifications;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ENotificationsRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class EduNotificationService {

    private final ENotificationsRepository notificationsRepository;

    public EduNotificationService(ENotificationsRepository notificationsRepository) {
        this.notificationsRepository = notificationsRepository;
    }

    public void triggerNotification(String userId, String title, String message, ENotificationType type, String relatedEntityId) {
        ENotifications notification = ENotifications.builder()
                .userId(userId)
                .title(title)
                .message(message)
                .type(type)
                .isRead(false)
                .relatedEntityId(relatedEntityId) // e.g., the newly published course ID or achievement
                .createdAt(Instant.now())
                .build();
                
        notificationsRepository.save(notification);
    }

    public List<ENotifications> getUserNotifications(String userId) {
        return notificationsRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<ENotifications> getUnreadNotifications(String userId) {
        return notificationsRepository.findByUserIdAndIsReadFalse(userId);
    }

    public ENotifications markAsRead(String notificationId) {
        ENotifications notification = notificationsRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        notification.setIsRead(true);
        return notificationsRepository.save(notification);
    }

    public void markAllAsRead(String userId) {
        List<ENotifications> unread = notificationsRepository.findByUserIdAndIsReadFalse(userId);
        unread.forEach(n -> n.setIsRead(true));
        notificationsRepository.saveAll(unread);
    }

    public long getUnreadCount(String userId) {
        return notificationsRepository.countByUserIdAndIsReadFalse(userId);
    }

    public void deleteNotification(String notificationId) {
        notificationsRepository.deleteById(notificationId);
    }

    public void markRead(List<String> notificationIds) {
        List<ENotifications> notifications = notificationsRepository.findAllById(notificationIds);
        notifications.forEach(n -> n.setIsRead(true));
        notificationsRepository.saveAll(notifications);
    }
}
