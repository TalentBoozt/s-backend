package com.talentboozt.s_backend.domains.community.repository.mongodb;

import com.talentboozt.s_backend.domains.community.model.CommunityNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommunityNotificationRepository extends MongoRepository<CommunityNotification, String> {
    List<CommunityNotification> findByRecipientIdOrderByTimestampDesc(String recipientId);

    Page<CommunityNotification> findByRecipientId(String recipientId, Pageable pageable);

    long countByRecipientIdAndIsReadFalse(String recipientId);
}
