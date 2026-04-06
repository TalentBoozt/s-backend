package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.model.EWebhookEvent;
import com.talentboozt.s_backend.domains.edu.model.EWebhookEvent.EventStatus;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EWebhookEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Manages webhook event lifecycle: deduplication, persistence, and DLQ.
 *
 * Flow:
 * 1. Controller receives event → calls tryAcquire(eventId)
 * 2. If already processed → returns false (skip)
 * 3. If new → inserts RETRYING record → returns true (process)
 * 4. After processing → calls markSuccess() or markFailed()
 * 5. Failed events are retried by WebhookRetryService on a schedule
 */
@Service
public class WebhookEventService {

    private static final Logger log = LoggerFactory.getLogger(WebhookEventService.class);

    private final EWebhookEventRepository repository;

    public WebhookEventService(EWebhookEventRepository repository) {
        this.repository = repository;
    }

    /**
     * Attempts to acquire processing rights for an event.
     * Returns true if this is the first time seeing this event ID.
     * Returns false if already processed (idempotency guard).
     *
     * Uses MongoDB unique index on stripeEventId as a distributed lock.
     */
    public boolean tryAcquire(String stripeEventId, String eventType, String payload, String sigHeader) {
        try {
            EWebhookEvent event = EWebhookEvent.builder()
                    .stripeEventId(stripeEventId)
                    .eventType(eventType)
                    .status(EventStatus.RETRYING)
                    .payload(payload)
                    .signatureHeader(sigHeader)
                    .retryCount(0)
                    .maxRetries(5)
                    .processedAt(Instant.now())
                    .build();
            repository.save(event);
            return true;
        } catch (DuplicateKeyException e) {
            // Event already exists — check if it was previously failed and needs reprocessing
            log.debug("Duplicate webhook event received: {} (already processed)", stripeEventId);
            return false;
        }
    }

    /**
     * Marks an event as successfully processed.
     */
    public void markSuccess(String stripeEventId) {
        repository.findByStripeEventId(stripeEventId).ifPresent(event -> {
            event.setStatus(EventStatus.SUCCESS);
            event.setErrorMessage(null);
            event.setErrorStackTrace(null);
            // Clear payload after success to save storage (signature no longer needed)
            event.setPayload(null);
            event.setSignatureHeader(null);
            repository.save(event);
        });
    }

    /**
     * Marks an event as failed. Calculates next retry time using exponential backoff.
     * If max retries exceeded, marks as DEAD.
     */
    public void markFailed(String stripeEventId, Exception error) {
        repository.findByStripeEventId(stripeEventId).ifPresent(event -> {
            int newRetryCount = (event.getRetryCount() != null ? event.getRetryCount() : 0) + 1;
            event.setRetryCount(newRetryCount);
            event.setErrorMessage(truncate(error.getMessage(), 500));
            event.setErrorStackTrace(truncate(getStackTrace(error), 2000));

            if (newRetryCount >= event.getMaxRetries()) {
                event.setStatus(EventStatus.DEAD);
                event.setNextRetryAt(null);
                log.error("Webhook event {} permanently failed after {} retries: {}",
                        stripeEventId, newRetryCount, error.getMessage());
            } else {
                event.setStatus(EventStatus.FAILED);
                // Exponential backoff: 5min, 25min, 2h, 10h, 50h
                long delayMinutes = (long) (5 * Math.pow(5, newRetryCount - 1));
                event.setNextRetryAt(Instant.now().plus(delayMinutes, ChronoUnit.MINUTES));
                log.warn("Webhook event {} failed (attempt {}/{}). Next retry at +{}min: {}",
                        stripeEventId, newRetryCount, event.getMaxRetries(),
                        delayMinutes, error.getMessage());
            }

            repository.save(event);
        });
    }

    /**
     * Returns the count of events in FAILED status (for health monitoring endpoints).
     */
    public long getFailedEventCount() {
        return repository.countByStatus(EventStatus.FAILED);
    }

    /**
     * Returns the count of permanently dead events (for alerting).
     */
    public long getDeadEventCount() {
        return repository.countByStatus(EventStatus.DEAD);
    }

    private static String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }

    private static String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
