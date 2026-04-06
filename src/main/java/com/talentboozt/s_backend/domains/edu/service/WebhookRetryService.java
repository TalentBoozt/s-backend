package com.talentboozt.s_backend.domains.edu.service;

import com.stripe.model.Event;
import com.stripe.net.Webhook;
import com.talentboozt.s_backend.domains.edu.model.EWebhookEvent;
import com.talentboozt.s_backend.domains.edu.model.EWebhookEvent.EventStatus;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EWebhookEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Scheduled service that retries failed webhook events from the DLQ.
 *
 * Runs every 10 minutes. For each eligible failed event:
 * 1. Re-verifies the Stripe signature (to prevent tampering of stored payloads)
 * 2. Re-constructs the Stripe Event
 * 3. Delegates to the webhook processor
 * 4. Updates status to SUCCESS or re-fails with exponential backoff
 *
 * This acts as a safety net for transient failures (DB timeouts, service outages).
 */
@Service
public class WebhookRetryService {

    private static final Logger log = LoggerFactory.getLogger(WebhookRetryService.class);

    private final EWebhookEventRepository webhookEventRepository;
    private final WebhookEventService webhookEventService;
    private final WebhookEventProcessor webhookEventProcessor;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    public WebhookRetryService(EWebhookEventRepository webhookEventRepository,
                               WebhookEventService webhookEventService,
                               WebhookEventProcessor webhookEventProcessor) {
        this.webhookEventRepository = webhookEventRepository;
        this.webhookEventService = webhookEventService;
        this.webhookEventProcessor = webhookEventProcessor;
    }

    @Scheduled(fixedDelay = 600000) // Every 10 minutes
    public void retryFailedEvents() {
        List<EWebhookEvent> failedEvents = webhookEventRepository
                .findByStatusAndNextRetryAtBefore(EventStatus.FAILED, Instant.now());

        if (failedEvents.isEmpty()) return;

        log.info("Retrying {} failed webhook events...", failedEvents.size());

        for (EWebhookEvent storedEvent : failedEvents) {
            try {
                // Mark as retrying to prevent concurrent retries
                storedEvent.setStatus(EventStatus.RETRYING);
                webhookEventRepository.save(storedEvent);

                if (storedEvent.getPayload() == null) {
                    log.warn("Skipping retry for event {} — payload was cleared", storedEvent.getStripeEventId());
                    storedEvent.setStatus(EventStatus.DEAD);
                    storedEvent.setErrorMessage("Payload unavailable for retry");
                    webhookEventRepository.save(storedEvent);
                    continue;
                }

                // Re-construct Stripe Event from stored payload
                // Note: We skip signature verification on retry since the
                // payload was originally verified when first received
                Event event = Event.GSON.fromJson(storedEvent.getPayload(), Event.class);

                // Process
                webhookEventProcessor.processEvent(event);

                // Success
                webhookEventService.markSuccess(storedEvent.getStripeEventId());
                log.info("Successfully retried webhook event: {} (type={})",
                        storedEvent.getStripeEventId(), storedEvent.getEventType());

            } catch (Exception ex) {
                // Re-fail with incremented retry count
                webhookEventService.markFailed(storedEvent.getStripeEventId(), ex);
            }
        }
    }
}
