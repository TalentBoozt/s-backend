package com.talentboozt.s_backend.Model.AUDIT_LOGS;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Lob;
import java.time.Instant;
import java.util.Date;

@Getter
@Setter

@Document(collection = "stripe_audit_logs")
public class StripeAuditLog {

    @Id
    private String id;

    private String eventId;
    private String eventType;
    private String sessionId;
    private String customerId;
    private String subscriptionId;
    private String paymentIntentId;

    private String status; // e.g., "processed", "error", "retry_pending"
    private String errorMessage;

    @Lob
    private String rawPayload; // Store the full event for tracing

    private int retryCount = 0;

    private Date createdAt = new Date();
    private Date updatedAt = new Date();

    @Indexed(name = "expireAtIndex", expireAfter = "0s")
    private Instant expiresAt;
}
