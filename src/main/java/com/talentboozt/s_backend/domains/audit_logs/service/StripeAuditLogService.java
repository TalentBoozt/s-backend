package com.talentboozt.s_backend.domains.audit_logs.service;

import com.stripe.model.Event;
import com.talentboozt.s_backend.domains.audit_logs.model.StripeAuditLog;
import com.talentboozt.s_backend.domains.audit_logs.repository.mongodb.StripeAuditLogRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class StripeAuditLogService {

    @Autowired
    private StripeAuditLogRepository auditLogRepository;

    @Value("${audit.expire-after-days:30}")
    private long expireAfterDays;

    /**
     * Enqueue an incoming Stripe event for asynchronous processing.
     * This creates (or updates) a log entry keyed by the Stripe event id.
     */
    public void enqueueEvent(Event event, String rawPayload) {
        String eventId = Objects.requireNonNull(event.getId(), "Stripe event id must not be null");

        StripeAuditLog log = auditLogRepository.findById(eventId).orElseGet(StripeAuditLog::new);
        log.setId(eventId);
        log.setEventId(eventId);
        log.setEventType(event.getType());
        log.setRawPayload(rawPayload);
        log.setStatus("retry_pending"); // ready for worker, with retries allowed
        log.setErrorMessage(null);
        log.setRetryCount(0);

        Date now = new Date();
        if (log.getCreatedAt() == null) {
            log.setCreatedAt(now);
        }
        log.setUpdatedAt(now);
        log.setExpiresAt(Instant.now().plus(expireAfterDays, ChronoUnit.DAYS)); // TTL

        auditLogRepository.save(log);
    }

    /**
     * Lightweight info log for Stripe events (does NOT participate in retry queue).
     */
    public void logEvent(Event event, String rawPayload) {
        StripeAuditLog log = new StripeAuditLog();
        log.setId(UUID.randomUUID().toString());
        log.setEventId(event.getId());
        log.setEventType(event.getType());

        // You can enrich this later with sessionId, customerId, etc.
        log.setRawPayload(rawPayload);
        log.setStatus("pending");
        Date now = new Date();
        log.setCreatedAt(now);
        log.setUpdatedAt(now);
        log.setExpiresAt(Instant.now().plus(expireAfterDays, ChronoUnit.DAYS)); // 30 days TTL
        auditLogRepository.save(log);
    }

    public void markProcessed(String eventId) {
        Optional<StripeAuditLog> logOpt = auditLogRepository.findById(Objects.requireNonNull(eventId));
        logOpt.ifPresent(log -> {
            log.setStatus("processed");
            log.setUpdatedAt(new Date());
            auditLogRepository.save(log);
        });
    }

    public void markFailed(String eventId, String error, boolean retryable) {
        Optional<StripeAuditLog> logOpt = auditLogRepository.findById(Objects.requireNonNull(eventId));
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
        log.setExpiresAt(Instant.now().plus(expireAfterDays, ChronoUnit.DAYS)); // 30 days TTL
        auditLogRepository.save(log);
    }
}
