package com.talentboozt.s_backend.domains.audit.service;

import com.talentboozt.s_backend.domains.audit.model.AuditLog;
import com.talentboozt.s_backend.domains.audit.repository.mongodb.AuditRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditRepository auditRepository;

    public void log(String userId, String orgId, String action, String resourceId, String resourceType, Map<String, Object> oldState, Map<String, Object> newState, String status, HttpServletRequest request) {
        AuditLog log = AuditLog.builder()
                .userId(userId)
                .organizationId(orgId)
                .action(action)
                .resourceId(resourceId)
                .resourceType(resourceType)
                .oldState(oldState)
                .newState(newState)
                .status(status)
                .ipAddress(request != null ? request.getRemoteAddr() : "unknown")
                .userAgent(request != null ? request.getHeader("User-Agent") : "unknown")
                .timestamp(Instant.now())
                .build();
        
        auditRepository.save(log);
    }
}
