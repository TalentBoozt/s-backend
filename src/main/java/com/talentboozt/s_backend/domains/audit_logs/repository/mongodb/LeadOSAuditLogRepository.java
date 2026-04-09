package com.talentboozt.s_backend.domains.audit_logs.repository.mongodb;

import com.talentboozt.s_backend.domains.audit_logs.model.LeadOSAuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface LeadOSAuditLogRepository extends MongoRepository<LeadOSAuditLog, String> {
    List<LeadOSAuditLog> findByWorkspaceIdOrderByTimestampDesc(String workspaceId);
}
