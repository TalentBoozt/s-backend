package com.talentboozt.s_backend.domains.common.controller;

import com.talentboozt.s_backend.domains.common.model.SystemNotificationsModel;
import com.talentboozt.s_backend.domains.common.service.SystemNotificationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/system-notifications")
public class SystemNotificationsController {
    @Autowired
    private SystemNotificationsService notificationService;

    @GetMapping("/active")
    public List<SystemNotificationsModel> getActiveNotifications() {
        return notificationService.getActiveNotifications();
    }

    @GetMapping("/all")
    public List<SystemNotificationsModel> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    @PostMapping("/add")
    public SystemNotificationsModel createNotification(@RequestBody SystemNotificationsModel notification) {
        return notificationService.createNotification(notification);
    }

    @PutMapping("/update/{id}")
    public SystemNotificationsModel updateNotification(@PathVariable String id, @RequestBody SystemNotificationsModel notification) {
        notification.setId(id);
        return notificationService.updateNotification(notification);
    }

    @PutMapping("/update-status/{id}")
    public SystemNotificationsModel updateNotificationStatus(@PathVariable String id) {
        return notificationService.updateNotificationStatus(id);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteNotification(@PathVariable String id) {
        notificationService.deleteNotification(id);
    }
}
