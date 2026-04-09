package com.talentboozt.s_backend.domains.audit_logs.service;

import com.talentboozt.s_backend.domains.audit_logs.model.LeadOSAuditLog;
import com.talentboozt.s_backend.domains.audit_logs.repository.mongodb.LeadOSAuditLogRepository;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class LAuditLogService {
    private final LeadOSAuditLogRepository repository;

    public LAuditLogService(LeadOSAuditLogRepository repository) {
        this.repository = repository;
    }

    public void log(String wsId, String userId, String action, String entityId, String entityType, Map<String, Object> details) {
        LeadOSAuditLog log = new LeadOSAuditLog();
        log.setWorkspaceId(wsId);
        log.setUserId(userId);
        log.setAction(action);
        log.setEntityId(entityId);
        log.setEntityType(entityType);
        log.setDetails(details);
        repository.save(log);
    }
}
