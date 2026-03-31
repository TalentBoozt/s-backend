package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.talentboozt.s_backend.domains.edu.model.EAuditLog;
import java.util.List;

@Repository
public interface EAuditLogRepository extends MongoRepository<EAuditLog, String> {
    List<EAuditLog> findByActorIdOrderByCreatedAtDesc(String actorId);
    List<EAuditLog> findByTargetIdOrderByCreatedAtDesc(String targetId);
}
