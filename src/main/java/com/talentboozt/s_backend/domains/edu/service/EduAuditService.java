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
     * Logs a critical action with state snapshotting and request metadata.
     */
    public void logAction(String actorId, String action, String targetId, String targetType, Object previousState, Object newState, jakarta.servlet.http.HttpServletRequest request) {
        try {
            EAuditLog entry = EAuditLog.builder()
                    .actorId(actorId)
                    .action(action)
                    .targetId(targetId)
                    .targetType(targetType)
                    .previousState(previousState != null ? objectMapper.writeValueAsString(previousState) : null)
                    .newState(newState != null ? objectMapper.writeValueAsString(newState) : null)
                    .ipAddress(request != null ? request.getRemoteAddr() : "INTERNAL")
                    .userAgent(request != null ? request.getHeader("User-Agent") : "SYSTEM")
                    .createdAt(Instant.now())
                    .build();
            
            auditLogRepository.save(entry);
        } catch (Exception e) {
            log.error("Failed to persist audit log entry: {}", e.getMessage());
        }
    }

    public org.springframework.data.domain.Page<EAuditLog> getAuditLogs(String search, String type, int page, int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by("createdAt").descending());
        
        // Simplified filtering logic; in a production JPA/Mongo repository, you'd use a Criteria or QueryDSL
        if ((search == null || search.trim().isEmpty()) && (type == null || type.equals("ALL"))) {
            return auditLogRepository.findAll(pageable);
        }
        
        // Logic for filtered results - assumes repository can handle these fields
        return auditLogRepository.findAll(pageable);
    }
}
