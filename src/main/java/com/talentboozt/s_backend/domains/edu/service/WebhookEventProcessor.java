package com.talentboozt.s_backend.domains.edu.service;

import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.Invoice;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Stateless processor for Stripe webhook events.
 *
 * Extracted from EduStripeWebhookController so that:
 * 1. Controller handles HTTP concerns (signature verification, response)
 * 2. This class handles business logic (event routing + delegation)
 * 3. WebhookRetryService can reuse the same processing logic for DLQ retries
 *
 * All methods here should be idempotent — safe to call multiple times
 * for the same event without side effects.
 */
@Service
public class WebhookEventProcessor {

    private static final Logger log = LoggerFactory.getLogger(WebhookEventProcessor.class);

    private final EduCoursePurchaseService coursePurchaseService;
    private final com.talentboozt.s_backend.domains.subscription.service.SubscriptionService subscriptionService;
    private final EduRefundService refundService;

    public WebhookEventProcessor(EduCoursePurchaseService coursePurchaseService,
            com.talentboozt.s_backend.domains.subscription.service.SubscriptionService subscriptionService,
            EduRefundService refundService) {
        this.coursePurchaseService = coursePurchaseService;
        this.subscriptionService = subscriptionService;
        this.refundService = refundService;
    }

    /**
     * Processes a verified Stripe event. Routes to the appropriate handler
     * based on event type. All handlers must be idempotent.
     *
     * @throws Exception if processing fails (caller decides retry/DLQ behavior)
     */
    public void processEvent(Event event) throws Exception {
        switch (event.getType()) {
            case "checkout.session.completed":
                handleCheckoutCompleted(event);
                break;
            case "checkout.session.expired":
                handleCheckoutExpired(event);
                break;
            case "customer.subscription.created":
            case "customer.subscription.updated":
            case "customer.subscription.deleted":
                handleSubscriptionEvent(event);
                break;
            case "invoice.payment_succeeded":
                handleInvoiceSucceeded(event);
                break;
            case "invoice.payment_failed":
                handleInvoiceFailed(event);
                break;
            case "charge.refunded":
                handleChargeRefunded(event);
                break;
            case "payment_intent.payment_failed":
                log.warn("Payment intent failed: event_id={}", event.getId());
                break;
            default:
                log.debug("Ignoring unhandled Stripe event type: {}", event.getType());
                break;
        }
    }

    private void handleCheckoutCompleted(Event event) throws Exception {
        Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
        if (session != null && session.getMetadata() != null) {
            String metaType = session.getMetadata().get("type");
            if (EduCoursePurchaseService.CHECKOUT_METADATA_TYPE.equals(metaType)
                    || "MULTI_COURSE_PURCHASE".equals(metaType)
                    || "BUNDLE_PURCHASE".equals(metaType)) {
                coursePurchaseService.secureFinalizePaidCourse(session.getId());
                log.info("Finalized edu course purchase for session {} (type={})",
                        session.getId(), metaType);
            }
        }
    }

    private void handleCheckoutExpired(Event event) {
        Session expiredSession = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
        if (expiredSession != null) {
            coursePurchaseService.markSessionExpired(expiredSession.getId());
            log.info("Marked expired checkout session: {}", expiredSession.getId());
        }
    }

    private void handleSubscriptionEvent(Event event) {
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
                    priceId);
            log.info("Handled {} for subscription {}", event.getType(), subscription.getId());
        }
    }

    private void handleInvoiceSucceeded(Event event) {
        Invoice invoice = (Invoice) event.getDataObjectDeserializer().getObject().orElse(null);
        if (invoice != null && invoice.getSubscription() != null) {
            subscriptionService.updatePaymentSucceeded(invoice.getCustomer());
            log.info("Handled invoice.payment_succeeded for customer {}", invoice.getCustomer());
        }
    }

    private void handleInvoiceFailed(Event event) {
        Invoice invoice = (Invoice) event.getDataObjectDeserializer().getObject().orElse(null);
        if (invoice != null && invoice.getSubscription() != null) {
            subscriptionService.updatePaymentFailed(invoice.getCustomer());
            log.info("Handled invoice.payment_failed for customer {}", invoice.getCustomer());
        }
    }

    private void handleChargeRefunded(Event event) {
        Charge charge = (Charge) event.getDataObjectDeserializer().getObject().orElse(null);
        if (charge == null) {
            log.warn("charge.refunded event {} had no charge data", event.getId());
            return;
        }

        String chargeId = charge.getId();
        long amountRefunded = charge.getAmountRefunded() != null ? charge.getAmountRefunded() : 0L;
        long amountTotal = charge.getAmount() != null ? charge.getAmount() : 0L;

        // Extract the latest refund ID from the charge's refund list
        String refundId = null;
        if (charge.getRefunds() != null && charge.getRefunds().getData() != null
                && !charge.getRefunds().getData().isEmpty()) {
            refundId = charge.getRefunds().getData().get(0).getId();
        }

        // Find the session ID from the payment intent metadata or charge metadata
        String sessionId = null;
        if (charge.getMetadata() != null) {
            sessionId = charge.getMetadata().get("checkout_session_id");
        }
        // Fallback: try payment_intent metadata
        if (sessionId == null && charge.getPaymentIntent() != null) {
            // The payment intent ID can be used to look up the session
            // For now, use the charge's payment_intent as a correlation key
            sessionId = charge.getPaymentIntent();
        }

        if (sessionId == null) {
            log.warn("charge.refunded event {} has no session/payment_intent correlation. " +
                    "chargeId={}, refundId={}", event.getId(), chargeId, refundId);
            return;
        }

        refundService.processStripeRefund(chargeId, refundId, amountRefunded, amountTotal, sessionId, null);
    }
}
