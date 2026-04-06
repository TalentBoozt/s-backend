package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import com.talentboozt.s_backend.domains.edu.model.EWebhookEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface EWebhookEventRepository extends MongoRepository<EWebhookEvent, String> {

    /** Check if a Stripe event ID has already been received. */
    boolean existsByStripeEventId(String stripeEventId);

    /** Find a previously stored event by Stripe event ID (for status check). */
    Optional<EWebhookEvent> findByStripeEventId(String stripeEventId);

    /** Find all failed events eligible for retry (nextRetryAt has passed). */
    List<EWebhookEvent> findByStatusAndNextRetryAtBefore(
            EWebhookEvent.EventStatus status, Instant cutoff);

    /** Find all permanently dead events (for admin dashboard / alerting). */
    List<EWebhookEvent> findByStatus(EWebhookEvent.EventStatus status);

    /** Count failed events (for health monitoring). */
    long countByStatus(EWebhookEvent.EventStatus status);
}
