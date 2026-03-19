package com.talentboozt.s_backend.domains.lifeplanner.notification.repository.mongodb;

import com.talentboozt.s_backend.domains.lifeplanner.notification.model.LPNotification;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface LPNotificationRepository extends MongoRepository<LPNotification, String> {
    List<LPNotification> findByUserIdOrderByCreatedAtDesc(String userId);
    int countByUserIdAndIsReadFalse(String userId);
}
