package com.talentboozt.s_backend.domains.community.service;

import com.talentboozt.s_backend.domains.community.model.Notification;
import com.talentboozt.s_backend.domains.community.repository.mongodb.NotificationRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void createNotification(String recipientId, String senderId, Notification.NotificationType type,
            String targetId) {
        if (recipientId.equals(senderId))
            return; // Don't notify self

        Notification notification = Notification.builder()
                .recipientId(recipientId)
                .senderId(senderId)
                .type(type)
                .targetId(targetId)
                .isRead(false)
                .timestamp(LocalDateTime.now())
                .build();
                Notification savedNotification = notificationRepository.save(Objects.requireNonNull(notification));

                // Push real-time notification
                messagingTemplate.convertAndSendToUser(
                        recipientId,
                        "/queue/notifications",
                        savedNotification);
    }

    public List<Notification> getNotifications(String userId) {
        return notificationRepository.findByRecipientIdOrderByTimestampDesc(userId);
    }

    public void markAsRead(String notificationId) {
        notificationRepository.findById(Objects.requireNonNull(notificationId)).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    public long getUnreadCount(String userId) {
        return notificationRepository.countByRecipientIdAndIsReadFalse(userId);
    }
}
