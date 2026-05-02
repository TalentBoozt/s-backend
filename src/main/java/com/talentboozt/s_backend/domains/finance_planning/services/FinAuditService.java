package com.talentboozt.s_backend.domains.finance_planning.services;

import com.talentboozt.s_backend.domains.finance_planning.models.FinAuditLog;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinAuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class FinAuditService {
    private final FinAuditLogRepository repository;

    public void log(String organizationId, String projectId, String userId, String action, String entityType, String entityId, Object oldVal, Object newVal) {
        FinAuditLog log = FinAuditLog.builder()
                .organizationId(organizationId)
                .projectId(projectId)
                .userId(userId)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .previousValue(oldVal)
                .newValue(newVal)
                .timestamp(Instant.now())
                .build();
        repository.save(log);
    }
}
