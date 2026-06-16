package com.talentboozt.s_backend.domains.portal.community.controller;

import com.talentboozt.s_backend.domains.portal.community.model.CommunityNotification;
import com.talentboozt.s_backend.domains.portal.community.service.CommunityNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v2/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final CommunityNotificationService notificationService;

    @GetMapping("/{userId}")
    public List<CommunityNotification> getNotifications(@PathVariable String userId) {
        return notificationService.getNotifications(userId);
    }

    @PutMapping("/{notificationId}/read")
    public void markAsRead(@PathVariable String notificationId) {
        notificationService.markAsRead(notificationId);
    }

    @GetMapping("/{userId}/unread-count")
    public long getUnreadCount(@PathVariable String userId) {
        return notificationService.getUnreadCount(userId);
    }
}
