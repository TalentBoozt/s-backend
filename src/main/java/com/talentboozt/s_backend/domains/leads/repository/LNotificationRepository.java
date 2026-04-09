package com.talentboozt.s_backend.domains.leads.repository;

import com.talentboozt.s_backend.domains.leads.model.LNotification;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface LNotificationRepository extends MongoRepository<LNotification, String> {
    List<LNotification> findByWorkspaceId(String workspaceId);
    List<LNotification> findByWorkspaceIdOrderByCreatedAtDesc(String workspaceId);
    long countByWorkspaceIdAndReadFalse(String workspaceId);
}
