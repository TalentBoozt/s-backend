package com.talentboozt.s_backend.domains.edu.controller;

import jakarta.validation.Valid;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import com.talentboozt.s_backend.domains.edu.service.WebhookEventProcessor;
import com.talentboozt.s_backend.domains.edu.service.WebhookEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Stripe webhook endpoint for the Edu payment system.
 *
 * Architecture:
 * 1. Signature verification (Stripe security)
 * 2. Idempotency check (WebhookEventService.tryAcquire)
 * 3. Event processing (WebhookEventProcessor — shared with retry service)
 * 4. DLQ on failure (WebhookEventService.markFailed → exponential backoff retry)
 *
 * Always returns 200 to Stripe to prevent infinite retries.
 * Failed events are retried internally via WebhookRetryService.
 */
@Slf4j
@RestController
@RequestMapping("/api/monetization/stripe")
@RequiredArgsConstructor
public class EduStripeWebhookController {

    private final WebhookEventProcessor eventProcessor;
    private final WebhookEventService webhookEventService;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @Valid @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        // Step 1: Verify Stripe signature
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.warn("Edu Stripe webhook signature failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("invalid signature");
        } catch (Exception e) {
            log.error("Edu Stripe webhook error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("webhook error");
        }

        // Step 2: Idempotency check — skip if already processed
        if (!webhookEventService.tryAcquire(event.getId(), event.getType(), payload, sigHeader)) {
            log.debug("Skipping duplicate webhook event: {}", event.getId());
            return ResponseEntity.ok("duplicate");
        }

        // Step 3: Process the event
        try {
            eventProcessor.processEvent(event);

            // Step 4a: Mark as successfully processed
            webhookEventService.markSuccess(event.getId());

        } catch (Exception ex) {
            // Step 4b: Mark as failed — DLQ with exponential backoff
            log.error("Failed to process Stripe webhook event {} (type={}): {}",
                    event.getId(), event.getType(), ex.getMessage(), ex);
            webhookEventService.markFailed(event.getId(), ex);
        }

        // Always return 200 — we handle retries internally
        return ResponseEntity.ok("ok");
    }
}
