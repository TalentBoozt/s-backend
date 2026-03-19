package com.talentboozt.s_backend.domains.lifeplanner.notification.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.talentboozt.s_backend.domains.lifeplanner.notification.model.LPNotification;
import com.talentboozt.s_backend.domains.lifeplanner.notification.service.LPNotificationService;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/api/lifeplanner/notifications")
@RequiredArgsConstructor
public class LPNotificationController {
    private final LPNotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<LPNotification>> getNotifications(@RequestHeader("x-user-id") String userId) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Integer> getUnreadCount(@RequestHeader("x-user-id") String userId) {
        return ResponseEntity.ok(notificationService.getUnreadCount(userId));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable String id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}
