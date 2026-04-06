package com.talentboozt.s_backend.domains.edu.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Stores every Stripe webhook event received by the edu system.
 * 
 * Serves two purposes:
 * 1. IDEMPOTENCY: Check if an event ID was already processed before re-processing
 * 2. DLQ (Dead Letter Queue): Failed events are stored with status=FAILED for retry
 * 
 * TTL index on processedAt ensures old successful events are auto-cleaned (90 days).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_webhook_events")
@CompoundIndex(name = "idx_status_retryAfter", def = "{'status': 1, 'nextRetryAt': 1}")
public class EWebhookEvent {
    @Id
    private String id;

    /** Stripe event ID (evt_xxx). Unique constraint prevents duplicate processing. */
    @Indexed(unique = true)
    private String stripeEventId;

    /** Stripe event type (e.g. checkout.session.completed) */
    @Indexed
    private String eventType;

    /** Processing status */
    @Indexed
    private EventStatus status;

    /** Raw JSON payload from Stripe (stored for retry) */
    private String payload;

    /** Stripe-Signature header (stored for retry verification) */
    private String signatureHeader;

    /** Error message if processing failed */
    private String errorMessage;

    /** Stack trace snippet for debugging (truncated to 2000 chars) */
    private String errorStackTrace;

    /** Number of retry attempts so far */
    @Builder.Default
    private Integer retryCount = 0;

    /** Maximum retries before permanently marking as DEAD */
    @Builder.Default
    private Integer maxRetries = 5;

    /** When the next retry should be attempted (exponential backoff) */
    private Instant nextRetryAt;

    @CreatedDate
    private Instant processedAt;

    public enum EventStatus {
        /** Successfully processed */
        SUCCESS,
        /** Processing failed — eligible for retry */
        FAILED,
        /** Permanently failed after max retries exhausted */
        DEAD,
        /** Currently being retried */
        RETRYING
    }
}
