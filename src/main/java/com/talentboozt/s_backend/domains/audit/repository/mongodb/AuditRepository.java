package com.talentboozt.s_backend.domains.audit.repository.mongodb;

import com.talentboozt.s_backend.domains.audit.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuditRepository extends MongoRepository<AuditLog, String> {
    Page<AuditLog> findByOrganizationIdOrderByTimestampDesc(String organizationId, Pageable pageable);
    Page<AuditLog> findByUserIdOrderByTimestampDesc(String userId, Pageable pageable);
}
