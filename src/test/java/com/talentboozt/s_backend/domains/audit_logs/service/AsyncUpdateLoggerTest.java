package com.talentboozt.s_backend.domains.audit_logs.service;

import com.talentboozt.s_backend.domains.audit_logs.model.AsyncUpdateAuditLog;
import com.talentboozt.s_backend.domains.audit_logs.repository.mongodb.AsyncUpdateAuditLogRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsyncUpdateLoggerTest {

    @Mock
    private AsyncUpdateAuditLogRepository auditLogRepository;

    @InjectMocks
    private AsyncUpdateLogger asyncUpdateLogger;

    @Test
    void createLogSavesLogWithPendingStatus() {
        AsyncUpdateAuditLog log = new AsyncUpdateAuditLog();
        log.setCourseId("course123");
        log.setBatchId("batch456");
        log.setOperation("updateCourse");
        log.setStatus("PENDING");
        log.setRetryCount(0);
        log.setCreatedAt(LocalDateTime.now());
        log.setUpdatedAt(LocalDateTime.now());
        log.setExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS));

        when(auditLogRepository.save(any(AsyncUpdateAuditLog.class))).thenReturn(log);

        AsyncUpdateAuditLog result = asyncUpdateLogger.createLog("course123", "batch456", "updateCourse");

        assertEquals("course123", result.getCourseId());
        assertEquals("batch456", result.getBatchId());
        assertEquals("updateCourse", result.getOperation());
        assertEquals("PENDING", result.getStatus());
        assertEquals(0, result.getRetryCount());
        verify(auditLogRepository).save(any(AsyncUpdateAuditLog.class));
    }

    @Test
    void markSuccessUpdatesLogStatusToSuccess() {
        AsyncUpdateAuditLog log = new AsyncUpdateAuditLog();
        log.setStatus("PENDING");

        asyncUpdateLogger.markSuccess(log);

        assertEquals("SUCCESS", log.getStatus());
        verify(auditLogRepository).save(log);
    }

    @Test
    void markFailureUpdatesLogStatusToFailedAndIncrementsRetryCount() {
        AsyncUpdateAuditLog log = new AsyncUpdateAuditLog();
        log.setStatus("PENDING");
        log.setRetryCount(1);

        Exception exception = new Exception("Error occurred");

        asyncUpdateLogger.markFailure(log, exception);

        assertEquals("FAILED", log.getStatus());
        assertEquals(2, log.getRetryCount());
        assertEquals("Error occurred", log.getErrorMessage());
        verify(auditLogRepository).save(log);
    }
}
