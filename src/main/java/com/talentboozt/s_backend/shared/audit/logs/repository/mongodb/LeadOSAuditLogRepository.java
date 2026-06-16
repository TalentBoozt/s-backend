package com.talentboozt.s_backend.shared.audit.logs.repository.mongodb;

import com.talentboozt.s_backend.shared.audit.logs.model.LeadOSAuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface LeadOSAuditLogRepository extends MongoRepository<LeadOSAuditLog, String> {
    List<LeadOSAuditLog> findByWorkspaceIdOrderByTimestampDesc(String workspaceId);
}
