package com.talentboozt.s_backend.domains.community.service;

import com.talentboozt.s_backend.domains.community.model.CommunityNotification;
import com.talentboozt.s_backend.domains.community.repository.mongodb.CommunityNotificationRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommunityNotificationService {
    private final CommunityNotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void createNotification(String recipientId, String senderId, CommunityNotification.NotificationType type,
            String targetId) {
        if (recipientId.equals(senderId))
            return; // Don't notify self

        CommunityNotification notification = CommunityNotification.builder()
                .recipientId(recipientId)
                .senderId(senderId)
                .type(type)
                .targetId(targetId)
                .isRead(false)
                .timestamp(LocalDateTime.now())
                .build();
                CommunityNotification savedNotification = notificationRepository.save(Objects.requireNonNull(notification));

                // Push real-time notification
                messagingTemplate.convertAndSendToUser(
                        recipientId,
                        "/queue/notifications",
                        savedNotification);
    }

    public List<CommunityNotification> getNotifications(String userId) {
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
