package com.talentboozt.s_backend.domains.payment.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.param.CustomerUpdateParams;
import com.talentboozt.s_backend.domains.payment.service.*;
import com.talentboozt.s_backend.domains.audit_logs.service.StripeAuditLogService;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@RestController
@RequestMapping("/stripe")
public class StripeWebhookController {

    @Autowired private StripeAuditLogService auditLogService;
    @Autowired private StripeService stripeService;

    private final String endpointSecret;
    private final Map<String, Consumer<Event>> EVENT_HANDLERS = new HashMap<>();

    public StripeWebhookController(@Value("${stripe.webhook.secret}") String endpointSecret) {
        this.endpointSecret = endpointSecret;
        initEventHandlers();
    }

    private void initEventHandlers() {
        EVENT_HANDLERS.put("checkout.session.completed", this::handleCheckoutSessionCompleted);
        EVENT_HANDLERS.put("invoice.payment_succeeded", this::handleInvoicePaymentSucceeded);
        EVENT_HANDLERS.put("invoice.payment_failed", this::handleInvoicePaymentFailed);
        EVENT_HANDLERS.put("invoice.created", this::handleInvoiceCreated);
        EVENT_HANDLERS.put("invoice.updated", this::handleInvoiceUpdated);
        EVENT_HANDLERS.put("customer.subscription.updated", this::handleSubscriptionUpdated);
        EVENT_HANDLERS.put("customer.subscription.deleted", this::handleSubscriptionDeleted);
    }

    @PostMapping("/create-checkout-session/{processType}")
    public ResponseEntity<Map<String, String>> createCheckoutSession(@RequestBody Map<String, Object> data, @PathVariable String processType) throws StripeException {
        Session session;

        switch (processType.toLowerCase()) {
            case "subscription":
                session = stripeService.createSubscriptionSession(data);
                break;

            case "course":
                session = stripeService.createCourseCheckoutSession(data, "course");
                break;

            case "course-onetime":
                session = stripeService.createCourseCheckoutSession(data, "course-onetime");
                break;

            // 🔮 Future: Handle coaching, mentorship, etc.
            // case "coaching":
            //     session = stripeService.createCoachingCheckoutSession(data);
            //     break;

            default:
                throw new IllegalArgumentException("Unsupported checkout process: " + processType);
        }

        Map<String, String> response = new HashMap<>();
        response.put("id", session.getId());
        response.put("url", session.getUrl());

        auditLogService.logCustom("checkout.session.created", "Created checkout session for type: " + processType + ", sessionId: " + session.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            handleEvent(event);
            return ResponseEntity.ok("Event processed");
        } catch (SignatureVerificationException e) {
            auditLogService.markFailed("N/A", "⚠️ Invalid Stripe signature", true);
            return ResponseEntity.badRequest().body("Invalid signature");
        } catch (Exception e) {
            auditLogService.markFailed("N/A", "❌ Exception: " + e.getMessage(), true);
            return ResponseEntity.internalServerError().body("Webhook error");
        }
    }

    public void handleEvent(Event event) {
        Consumer<Event> handler = EVENT_HANDLERS.get(event.getType());
        if (handler != null) {
            try {
                handler.accept(event);
                auditLogService.logEvent(event, "✅ Processed: " + event.getType());
            } catch (Exception e) {
                auditLogService.markFailed(event.getId(), "❌ Handler failed: " + e.getMessage(), true);
            }
        } else {
            auditLogService.markFailed(event.getId(), "⚠️ Unhandled event type: " + event.getType(), false);
        }
    }

    // ------------------- EVENT HANDLERS -------------------

    private void handleCheckoutSessionCompleted(Event event) {
        Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
        if (session == null) {
            auditLogService.markFailed(event.getId(), "No session object", true);
            return;
        }

        String purchaseType = session.getMetadata().get("purchase_type");
        if ("course".equals(purchaseType)) {
            handleCoursePurchase(event, session, false);
        } else if ("course-onetime".equals(purchaseType)) {
            handleCoursePurchase(event, session, true);
        } else if ("subscription".equals(purchaseType)) {
            try {
                handleSubscriptionPurchase(event, session);
            } catch (StripeException e) {
                auditLogService.markFailed(event.getId(), "❌ Stripe error: " + e.getMessage(), true);
            }
        } else {
            auditLogService.markFailed(event.getId(), "Unknown purchase_type: " + purchaseType, false);
        }
    }

    private void handleInvoicePaymentSucceeded(Event event) {
        // Example: store invoice in DB
        stripeService.storeInvoice((Invoice) event.getDataObjectDeserializer().getObject().orElse(null), null);
    }

    private void handleInvoicePaymentFailed(Event event) {
        auditLogService.logEvent(event, "⚠️ Invoice payment failed");
    }

    private void handleInvoiceCreated(Event event) {
        stripeService.storeInvoice((Invoice) event.getDataObjectDeserializer().getObject().orElse(null), null);
    }

    private void handleInvoiceUpdated(Event event) {
        stripeService.updateInvoice((Invoice) event.getDataObjectDeserializer().getObject().orElse(null));
    }

    private void handleSubscriptionUpdated(Event event) {
        stripeService.updateSubscription((Subscription) event.getDataObjectDeserializer().getObject().orElse(null));
    }

    private void handleSubscriptionDeleted(Event event) {
        stripeService.deleteSubscription((Subscription) event.getDataObjectDeserializer().getObject().orElse(null));
    }

    // ------------------- HELPERS -------------------

    private void handleCoursePurchase(Event event, Session session, boolean oneTime) {
        try {
            Map<String, String> metadata = session.getMetadata();
            String userId = metadata.get("user_id");
            String courseId = metadata.get("course_id");
            String installmentId = metadata.get("installment_id");

            // Attach metadata to Customer
            if (session.getCustomer() != null) {
                Customer customer = Customer.retrieve(session.getCustomer());
                CustomerUpdateParams updateParams = CustomerUpdateParams.builder()
                        .putMetadata("user_id", userId)
                        .putMetadata("course_id", courseId)
                        .putMetadata("installment_id", installmentId)
                        .build();
                customer.update(updateParams);
            }

            // Store billing + payment info
            stripeService.createBillingHistory(userId, session.getId(), session, "course");
            stripeService.createPaymentMethod(userId, session, "course");

            // Update course status
            if (oneTime) {
                Invoice invoice = stripeService.createManualInvoiceForOneTime(session, "Full course payment");
                stripeService.storeInvoice(invoice, session.getId());
                stripeService.updateFullCoursePayment(userId, courseId, installmentId);
            } else {
                Invoice invoice = stripeService.createManualInvoiceForOneTime(session, "Course installment payment");
                stripeService.storeInvoice(invoice, session.getId());
                stripeService.updateCourseInstallmentPayment(userId, courseId, installmentId);
            }

            auditLogService.markProcessed(event.getId());
        } catch (Exception e) {
            auditLogService.markFailed(event.getId(), "❌ Course purchase failed: " + e.getMessage(), true);
        }
    }

    private void handleSubscriptionPurchase(Event event, Session session) throws StripeException {
        Map<String, String> metadata = session.getMetadata();
        if (session.getCustomer() == null) {
            auditLogService.markFailed(event.getId(), "Session has no customer ID (one-time subscription purchase?)", true);
            return;
        }
        Customer customer = Customer.retrieve(session.getCustomer());

        CustomerUpdateParams updateParams = CustomerUpdateParams.builder()
                .putMetadata("company_id", metadata.get("company_id"))
                .putMetadata("plan_name", metadata.get("plan_name"))
                .build();

        customer.update(updateParams);
        auditLogService.logEvent(event, "Updated customer metadata for company_id: " + metadata.get("company_id"));

        String companyId = session.getMetadata().get("company_id");
        String planName = session.getMetadata().get("plan_name");

        if (companyId == null || planName == null) {
            auditLogService.markFailed(event.getId(), "Missing required metadata: company_id or plan_name", true);
            return;
        }
        try {
            stripeService.createSubscriptionWH(companyId, planName, session);
            stripeService.createBillingHistory(companyId, session.getId(), session, "subscription");
            stripeService.createPaymentMethod(companyId, session, "subscription");
            stripeService.updateCompanyStatus(companyId, session);
            auditLogService.markProcessed(event.getId());
        } catch (StripeException e) {
            auditLogService.markFailed(event.getId(), "Error creating subscription for company: " + companyId, true);
        }
    }
}
