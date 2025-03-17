package com.talentboozt.s_backend.Service.common;

import com.talentboozt.s_backend.Model.common.SystemNotificationsModel;
import com.talentboozt.s_backend.Repository.common.SystemNotificationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
        return notificationRepository.save(notification);
    }

    public SystemNotificationsModel updateNotification(SystemNotificationsModel notification) {
        return notificationRepository.save(notification);
    }

    public SystemNotificationsModel updateNotificationStatus(String id) {
        Optional<SystemNotificationsModel> notification = notificationRepository.findById(id);
        if (notification.isPresent()) {
            SystemNotificationsModel notificationObj = notification.get();
            notificationObj.setActive(!notificationObj.isActive());
            return notificationRepository.save(notificationObj);
        }
        return null;
    }

    public void deleteNotification(String id) {
        notificationRepository.deleteById(id);
    }
}
