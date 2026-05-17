package com.talentboozt.s_backend.domains.notifications.repository.mongodb;

import com.talentboozt.s_backend.domains.notifications.model.NotificationModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface NotificationRepository extends MongoRepository<NotificationModel, String> {
    List<NotificationModel> findByUserIdOrderByCreatedAtDesc(String userId);
}
