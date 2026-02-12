package com.talentboozt.s_backend.domains.community.repository.mongodb;

import com.talentboozt.s_backend.domains.community.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByRecipientIdOrderByTimestampDesc(String recipientId);

    Page<Notification> findByRecipientId(String recipientId, Pageable pageable);

    long countByRecipientIdAndIsReadFalse(String recipientId);
}
