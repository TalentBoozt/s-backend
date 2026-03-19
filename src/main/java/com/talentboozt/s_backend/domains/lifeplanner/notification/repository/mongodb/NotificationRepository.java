package com.talentboozt.s_backend.domains.lifeplanner.notification.repository.mongodb;

import com.talentboozt.s_backend.domains.lifeplanner.notification.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);
    int countByUserIdAndIsReadFalse(String userId);
}
