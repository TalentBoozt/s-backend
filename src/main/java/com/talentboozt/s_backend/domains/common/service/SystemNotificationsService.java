package com.talentboozt.s_backend.domains.common.service;

import com.talentboozt.s_backend.domains.common.model.SystemNotificationsModel;
import com.talentboozt.s_backend.domains.common.repository.mongodb.SystemNotificationsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SystemNotificationsService {
    @Autowired
    private SystemNotificationsRepository notificationRepository;

    public List<SystemNotificationsModel> getActiveNotifications() {
        return notificationRepository.findByActive(true);
    }

    public List<SystemNotificationsModel> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public SystemNotificationsModel createNotification(SystemNotificationsModel notification) {
        return notificationRepository.save(Objects.requireNonNull(notification));
    }

    public SystemNotificationsModel updateNotification(SystemNotificationsModel notification) {
        return notificationRepository.save(Objects.requireNonNull(notification));
    }

    public SystemNotificationsModel updateNotificationStatus(String id) {
        Optional<SystemNotificationsModel> notification = notificationRepository.findById(Objects.requireNonNull(id));
        if (notification.isPresent()) {
            SystemNotificationsModel notificationObj = notification.get();
            notificationObj.setActive(!notificationObj.isActive());
            return notificationRepository.save(notificationObj);
        }
        return null;
    }

    public void deleteNotification(String id) {
        notificationRepository.deleteById(Objects.requireNonNull(id));
    }
}
