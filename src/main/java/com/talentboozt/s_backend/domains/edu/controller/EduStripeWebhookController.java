package com.talentboozt.s_backend.domains.edu.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.Invoice;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.talentboozt.s_backend.domains.edu.service.EduCoursePurchaseService;
import com.talentboozt.s_backend.domains.edu.service.EduSubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/monetization/stripe")
@RequiredArgsConstructor
public class EduStripeWebhookController {

    private final EduCoursePurchaseService coursePurchaseService;
    private final EduSubscriptionService subscriptionService;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
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

        try {
            switch (event.getType()) {
                case "checkout.session.completed":
                    Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
                    if (session != null && session.getMetadata() != null
                            && EduCoursePurchaseService.CHECKOUT_METADATA_TYPE.equals(session.getMetadata().get("type"))) {
                        coursePurchaseService.finalizePaidCourseIfReady(session.getId());
                        log.info("Finalized edu course purchase for session {}", session.getId());
                    }
                    break;
                case "customer.subscription.created":
                case "customer.subscription.updated":
                case "customer.subscription.deleted":
                    Subscription subscription = (Subscription) event.getDataObjectDeserializer().getObject().orElse(null);
                    if (subscription != null) {
                        String priceId = null;
                        if (subscription.getItems() != null && !subscription.getItems().getData().isEmpty()) {
                            priceId = subscription.getItems().getData().get(0).getPrice().getId();
                        }
                        subscriptionService.updateFromStripeEvent(
                                subscription.getCustomer(),
                                subscription.getId(),
                                subscription.getStatus(),
                                priceId
                        );
                        log.info("Handled {} for subscription {}", event.getType(), subscription.getId());
                    }
                    break;
                case "invoice.payment_succeeded":
                    Invoice succeededInvoice = (Invoice) event.getDataObjectDeserializer().getObject().orElse(null);
                    if (succeededInvoice != null && succeededInvoice.getSubscription() != null) {
                        subscriptionService.updatePaymentSucceeded(succeededInvoice.getCustomer());
                        log.info("Handled invoice.payment_succeeded for customer {}", succeededInvoice.getCustomer());
                    }
                    break;
                case "invoice.payment_failed":
                    Invoice failedInvoice = (Invoice) event.getDataObjectDeserializer().getObject().orElse(null);
                    if (failedInvoice != null && failedInvoice.getSubscription() != null) {
                        subscriptionService.updatePaymentFailed(failedInvoice.getCustomer());
                        log.info("Handled invoice.payment_failed for customer {}", failedInvoice.getCustomer());
                    }
                    break;
                default:
                    // Ignored event type
                    break;
            }
        } catch (Exception ex) {
            log.error("Failed to process Stripe webhook event {}: {}", event.getType(), ex.getMessage(), ex);
            // We still return 200 OK so Stripe doesn't endlessly retry if it's a code issue on our end
            // but we've logged it
        }

        return ResponseEntity.ok("ok");
    }
}
