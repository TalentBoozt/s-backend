package com.talentboozt.s_backend.Service.SYS_TRACKING;

import com.stripe.model.Event;
import com.talentboozt.s_backend.Model.SYS_TRACKING.StripeAuditLog;
import com.talentboozt.s_backend.Repository.SYS_TRACKING.StripeAuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StripeAuditLogService {

    @Autowired
    private StripeAuditLogRepository auditLogRepository;

    public void logEvent(Event event, String rawPayload) {
        StripeAuditLog log = new StripeAuditLog();
        log.setEventId(event.getId());
        log.setEventType(event.getType());

        // You can enrich this later with sessionId, customerId, etc.
        log.setRawPayload(rawPayload);
        log.setStatus("pending");
        log.setCreatedAt(new Date());
        log.setExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS)); // 30 days TTL
        auditLogRepository.save(log);
    }

    public void markProcessed(String eventId) {
        Optional<StripeAuditLog> logOpt = auditLogRepository.findById(eventId);
        logOpt.ifPresent(log -> {
            log.setStatus("processed");
            log.setUpdatedAt(new Date());
            auditLogRepository.save(log);
        });
    }

    public void markFailed(String eventId, String error, boolean retryable) {
        Optional<StripeAuditLog> logOpt = auditLogRepository.findById(eventId);
        logOpt.ifPresent(log -> {
            log.setStatus(retryable ? "retry_pending" : "error");
            log.setErrorMessage(error);
            log.setRetryCount(log.getRetryCount() + 1);
            log.setUpdatedAt(new Date());
            auditLogRepository.save(log);
        });
    }

    public List<StripeAuditLog> getLogsForRetry(int maxRetries) {
        return auditLogRepository.findTop20ByStatusAndRetryCountLessThanOrderByCreatedAtAsc("retry_pending", maxRetries);
    }

    public void logCustom(String title, String message) {
        StripeAuditLog log = new StripeAuditLog();
        log.setEventId(UUID.randomUUID().toString());
        log.setEventType(title);
        log.setRawPayload(message);
        log.setStatus("info");
        log.setCreatedAt(new Date());
        log.setExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS)); // 30 days TTL
        auditLogRepository.save(log);
    }
}
