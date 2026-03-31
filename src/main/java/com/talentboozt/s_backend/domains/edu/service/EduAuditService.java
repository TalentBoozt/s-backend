package com.talentboozt.s_backend.domains.edu.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentboozt.s_backend.domains.edu.model.EAuditLog;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EAuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class EduAuditService {

    private final EAuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    /**
     * Logs a critical action with state snapshotting.
     */
    public void logAction(String actorId, String action, String targetId, String targetType, Object previousState, Object newState) {
        try {
            EAuditLog entry = EAuditLog.builder()
                    .actorId(actorId)
                    .action(action)
                    .targetId(targetId)
                    .targetType(targetType)
                    .previousState(previousState != null ? objectMapper.writeValueAsString(previousState) : null)
                    .newState(newState != null ? objectMapper.writeValueAsString(newState) : null)
                    .createdAt(Instant.now())
                    .build();
            
            auditLogRepository.save(entry);
        } catch (Exception e) {
            log.error("Failed to persist audit log entry: {}", e.getMessage());
            // We don't throw an exception here to avoid breaking the calling business logic
            // but in a high-security environment, we might want to enforce auditing.
        }
    }
}
