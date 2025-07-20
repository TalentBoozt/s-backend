package com.talentboozt.s_backend.domains.audit_logs.service;

import com.talentboozt.s_backend.domains.audit_logs.model.AsyncUpdateAuditLog;
import com.talentboozt.s_backend.domains.audit_logs.repository.AsyncUpdateAuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class AsyncUpdateLogger {

    @Autowired
    private AsyncUpdateAuditLogRepository auditLogRepository;

    @Value("${audit.expire-after-days:30}")
    private long expireAfterDays;

    public AsyncUpdateAuditLog createLog(String courseId, String batchId, String operation) {
        AsyncUpdateAuditLog log = new AsyncUpdateAuditLog();
        log.setCourseId(courseId);
        log.setBatchId(batchId);
        log.setOperation(operation);
        log.setStatus("PENDING");
        log.setRetryCount(0);
        log.setCreatedAt(LocalDateTime.now());
        log.setUpdatedAt(LocalDateTime.now());
        log.setExpiresAt(Instant.now().plus(expireAfterDays, ChronoUnit.DAYS));
        return auditLogRepository.save(log);
    }

    public void markSuccess(AsyncUpdateAuditLog log) {
        log.setStatus("SUCCESS");
        log.setUpdatedAt(LocalDateTime.now());
        auditLogRepository.save(log);
    }

    public void markFailure(AsyncUpdateAuditLog log, Exception e) {
        log.setStatus("FAILED");
        log.setRetryCount(log.getRetryCount() + 1);
        log.setErrorMessage(e.getMessage());
        log.setUpdatedAt(LocalDateTime.now());
        auditLogRepository.save(log);
    }
}
