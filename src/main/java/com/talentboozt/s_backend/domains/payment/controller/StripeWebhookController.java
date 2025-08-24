package com.talentboozt.s_backend.domains.payment.controller;

import com.stripe.param.CustomerUpdateParams;
import com.talentboozt.s_backend.domains.payment.model.BillingHistoryModel;
import com.talentboozt.s_backend.domains.payment.model.InvoicesModel;
import com.talentboozt.s_backend.domains.payment.model.PaymentMethodsModel;
import com.talentboozt.s_backend.domains.payment.model.SubscriptionsModel;
import com.talentboozt.s_backend.domains.payment.repository.BillingHistoryRepository;
import com.talentboozt.s_backend.domains.payment.repository.InvoiceRepository;
import com.talentboozt.s_backend.domains.payment.repository.PaymentMethodRepository;
import com.talentboozt.s_backend.domains.com_job_portal.service.CmpPostedJobsService;
import com.talentboozt.s_backend.domains.com_job_portal.service.CompanyService;
import com.talentboozt.s_backend.domains.payment.service.*;
import com.talentboozt.s_backend.domains.plat_courses.service.EmpCoursesService;
import com.talentboozt.s_backend.domains.audit_logs.service.StripeAuditLogService;
import com.talentboozt.s_backend.domains.auth.service.CredentialsService;
import com.talentboozt.s_backend.shared.utils.ConfigUtility;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@RestController
@RequestMapping("/stripe")
public class StripeWebhookController {

    @Autowired
    ConfigUtility configUtility;

    @Autowired
    PaymentMethodRepository paymentMethodRepository;

    @Autowired
    BillingHistoryRepository billingHistoryRepository;

    @Autowired
    InvoiceRepository invoiceRepository;

    @Autowired
    StripeService stripeService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private BillingHistoryService billingHistoryService;

    @Autowired
    private PaymentMethodService paymentMethodService;

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CmpPostedJobsService cmpPostedJobsService;

    @Autowired
    private PrePaymentService prePaymentService;

    @Autowired
    private EmpCoursesService empCoursesService;

    @Autowired
    private StripeAuditLogService auditLogService;

    @PostMapping("/stripe-events")
    public ResponseEntity<String> handleStripeEvent(@RequestBody String payload) {
        Event event = Event.GSON.fromJson(payload, Event.class);
        handleEvent(event);
        return ResponseEntity.ok("Event processed");
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
            Event event = Webhook.constructEvent(payload, sigHeader, configUtility.getProperty("STRIPE_WEBHOOK_SECRET"));
            auditLogService.logEvent(event, payload);
            handleEvent(event);
            auditLogService.markProcessed(event.getId());
            return ResponseEntity.ok("Webhook processed successfully");
        } catch (Exception e) {
            auditLogService.markFailed("unknown", e.getMessage(), true);
            return ResponseEntity.badRequest().body("Webhook error: " + e.getMessage());
        }
    }

    public void handleEvent(Event event) {
        Map<String, Consumer<Event>> eventHandlers = Map.of(
                "checkout.session.completed", this::handleCheckoutSessionCompleted,
                "invoice.payment_succeeded", this::handleInvoicePaymentSucceeded,
                "invoice.payment_failed", this::handleInvoicePaymentFailed,
                "invoice.created", this::handleInvoiceCreated,
                "invoice.updated", this::handleInvoiceUpdated,
                "customer.subscription.updated", this::handleSubscriptionUpdated,
                "customer.subscription.deleted", this::handleSubscriptionDeleted
        );

        Consumer<Event> handler = eventHandlers.getOrDefault(event.getType(), this::handleUnhandledEvent);
        handler.accept(event);
        auditLogService.logEvent(event, "Routing Stripe event: " + event.getType());
    }

    private void handleCheckoutSessionCompleted(Event event) {
        try {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session == null || session.getMetadata() == null) {
                auditLogService.markFailed(event.getId(), "Session or metadata is null", true);
                return;
            }

            if (session.getId() != null) {
                session = Session.retrieve(session.getId());
            }

            String purchaseType = session.getMetadata().get("purchase_type");

            if ("course".equals(purchaseType)) {
                Map<String, String> metadata = session.getMetadata();

                if (session.getCustomer() != null) {
                    Customer customer = Customer.retrieve(session.getCustomer());

                    CustomerUpdateParams updateParams = CustomerUpdateParams.builder()
                            .putMetadata("user_id", metadata.get("user_id"))
                            .putMetadata("course_id", metadata.get("course_id"))
                            .putMetadata("installment_id", metadata.get("installment_id"))
                            .build();

                    customer.update(updateParams);
                    auditLogService.logEvent(event, "Updated customer metadata for user_id: " + metadata.get("user_id"));
                } else {
                    auditLogService.markFailed(event.getId(), "Session has no customer ID (one-time course purchase?)", false);
                }

                String userId = session.getMetadata().get("user_id");
                String courseId = session.getMetadata().get("course_id");
                String installmentId = session.getMetadata().get("installment_id");

                if (userId == null || courseId == null || installmentId == null) {
                    auditLogService.markFailed(event.getId(), "Missing required metadata: user_id, course_id or installment_id", true);
                    return;
                }
                try {
                    createBillingHistory(userId, session.getId(), session, "course");
                } catch (Exception e) {
                    auditLogService.markFailed(event.getId(), "createBillingHistory failed: " + e.getMessage(), true);
                }

                try {
                    createPaymentMethod(userId, session, "course");
                } catch (Exception e) {
                    auditLogService.markFailed(event.getId(), "createPaymentMethod failed: " + e.getMessage(), true);
                }

                try {
                    updateCourseInstallmentPayment(userId, courseId, installmentId);
                } catch (Exception e) {
                    auditLogService.markFailed(event.getId(), "updateCourseInstallmentPayment failed: " + e.getMessage(), true);
                }
                auditLogService.markProcessed(event.getId());
            } else if ("course-onetime".equals(purchaseType)) {
                Map<String, String> metadata = session.getMetadata();

                if (session.getCustomer() != null) {
                    Customer customer = Customer.retrieve(session.getCustomer());

                    CustomerUpdateParams updateParams = CustomerUpdateParams.builder()
                            .putMetadata("user_id", metadata.get("user_id"))
                            .putMetadata("course_id", metadata.get("course_id"))
                            .putMetadata("installment_id", metadata.get("installment_id"))
                            .build();

                    customer.update(updateParams);
                    auditLogService.logEvent(event, "Updated customer metadata for user_id: " + metadata.get("user_id"));
                } else {
                    auditLogService.markFailed(event.getId(), "Session has no customer ID (one-time course purchase?)", false);
                }

                String userId = session.getMetadata().get("user_id");
                String courseId = session.getMetadata().get("course_id");
                String installmentId = session.getMetadata().get("installment_id");

                if (userId == null || courseId == null || installmentId == null) {
                    auditLogService.markFailed(event.getId(), "Missing required metadata: user_id, course_id or installment_id", true);
                    return;
                }
                try {
                    createBillingHistory(userId, session.getId(), session, "course");
                } catch (Exception e) {
                    auditLogService.markFailed(event.getId(), "createBillingHistory failed: " + e.getMessage(), true);
                }

                try {
                    createPaymentMethod(userId, session, "course");
                } catch (Exception e) {
                    auditLogService.markFailed(event.getId(), "createPaymentMethod failed: " + e.getMessage(), true);
                }

                try {
                    updateFullCoursePayment(userId, courseId, installmentId);
                } catch (Exception e) {
                    auditLogService.markFailed(event.getId(), "updateCourseInstallmentPayment failed: " + e.getMessage(), true);
                }
                auditLogService.markProcessed(event.getId());
            } else if ("subscription".equals(purchaseType)) {
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
                    createSubscription(companyId, planName, session);
                    createBillingHistory(companyId, session.getId(), session, "subscription");
                    createPaymentMethod(companyId, session, "subscription");
                    updateCompanyStatus(companyId, session);
                    auditLogService.markProcessed(event.getId());
                } catch (StripeException e) {
                    auditLogService.markFailed(event.getId(), "Error creating subscription for company: " + companyId, true);
                }
            }

        } catch (Exception e) {
            auditLogService.markFailed(event.getId(), "Error handling checkout.session.completed: " + e.getMessage(), true);
        }
    }

    private void handleInvoiceCreated(Event event) {
        Invoice invoice = (Invoice) event.getDataObjectDeserializer().getObject().orElse(null);
        if (invoice == null) {
            auditLogService.markFailed(event.getId(), "Invoice object is null", true);
            return;
        }
        try {
            createInvoice(invoice);
            auditLogService.markProcessed(event.getId());
        } catch (StripeException e) {
            auditLogService.markFailed(event.getId(), "Error creating invoice: " + e.getMessage(), true);
        }
    }

    private void handleInvoiceUpdated(Event event) {
        Invoice invoice = (Invoice) event.getDataObjectDeserializer().getObject().orElse(null);
        if (invoice == null) {
            auditLogService.markFailed(event.getId(), "Invoice object is null", true);
            return;
        }
        try {
            updateInvoice(invoice);
            auditLogService.markProcessed(event.getId());
        } catch (StripeException e) {
            auditLogService.markFailed(event.getId(), "Error updating invoice: " + e.getMessage(), true);
        }
    }

    private void handleInvoicePaymentSucceeded(Event event) {
        Invoice invoice = (Invoice) event.getDataObjectDeserializer().getObject().orElse(null);
        if (invoice == null) {
            auditLogService.markFailed(event.getId(), "Invoice object is null", true);
            return;
        }
        String subscriptionId = invoice.getSubscription();
        String amountPaid = String.valueOf(invoice.getAmountPaid());

        // Update subscription billing history
        subscriptionService.updateBillingHistory(subscriptionId, amountPaid, "Paid");
        auditLogService.logEvent(event, "Updated subscription billing history for subscriptionId: " + subscriptionId);
    }

    private void handleInvoicePaymentFailed(Event event) {
        Invoice invoice = (Invoice) event.getDataObjectDeserializer().getObject().orElse(null);
        if (invoice == null) {
            auditLogService.markFailed(event.getId(), "Invoice object is null", true);
            return;
        }
        String subscriptionId = invoice.getSubscription();

        // Update subscription as inactive
        subscriptionService.markAsInactive(subscriptionId);
        auditLogService.logEvent(event, "Marked subscription as inactive for subscriptionId: " + subscriptionId);
    }

    private void handleUnhandledEvent(Event event) {
        auditLogService.markFailed(event.getId(), "Unhandled event type: " + event.getType(), false);
    }

    private void createInvoice(Invoice invoice) throws StripeException {
        String companyId;

        // Retrieve the associated customer
        Customer customer = Customer.retrieve(invoice.getCustomer());
        if (customer.getMetadata() != null && customer.getMetadata().containsKey("company_id")) {
            companyId = customer.getMetadata().get("company_id");
        } else if (invoice.getLines().getData().get(0).getMetadata() != null && invoice.getLines().getData().get(0).getMetadata().containsKey("company_id")) {
            companyId = invoice.getLines().getData().get(0).getMetadata().get("company_id");
        } else {
            companyId = null;
        }

        if (companyId == null) {
            auditLogService.markFailed(invoice.getId(), "Missing required metadata: company_id", true);
            return;
        }

        InvoicesModel invoiceModel = new InvoicesModel();
        invoiceModel.setInvoiceId(invoice.getId());
        invoiceModel.setCompanyId(companyId);
        invoiceModel.setSubscriptionId(invoice.getSubscription());
        invoiceModel.setAmountDue(String.valueOf(invoice.getAmountDue()));
        invoiceModel.setStatus(invoice.getStatus());
        invoiceModel.setBillingDate(new Date(invoice.getCreated() * 1000L));
        Date dueDate = invoice.getDueDate() != null
                ? new Date(invoice.getDueDate() * 1000L)
                : new Date((invoice.getCreated() + (7 * 24 * 60 * 60)) * 1000L); // fallback: created + 7 days
        invoiceModel.setDueDate(dueDate);
        invoiceModel.setPeriodStart(new Date(invoice.getPeriodStart() * 1000L));
        invoiceModel.setPeriodEnd(new Date(invoice.getPeriodEnd() * 1000L));
        invoiceModel.setInvoice_pdf(invoice.getInvoicePdf());
        invoiceModel.setHosted_invoice_url(invoice.getHostedInvoiceUrl());

        invoiceRepository.save(invoiceModel);
        prePaymentService.updateInvoiceId(invoice.getId(), companyId);
    }

    private void updateInvoice(Invoice invoice) throws StripeException {
        String companyId;

        Customer customer = Customer.retrieve(invoice.getCustomer());
        if (customer.getMetadata() != null && customer.getMetadata().containsKey("company_id")) {
            companyId = customer.getMetadata().get("company_id");
        } else if (invoice.getLines().getData().get(0).getMetadata() != null && invoice.getLines().getData().get(0).getMetadata().containsKey("company_id")) {
            companyId = invoice.getLines().getData().get(0).getMetadata().get("company_id");
        } else {
            companyId = null;
        }

        if (companyId == null) {
            auditLogService.markFailed(invoice.getId(), "Missing required metadata: company_id", true);
            return;
        }

        InvoicesModel existingInvoice = invoiceRepository.findByInvoiceId(invoice.getId());
        if (existingInvoice != null) {
            existingInvoice.setAmountDue(String.valueOf(invoice.getAmountDue()));
            existingInvoice.setStatus(invoice.getStatus());
            existingInvoice.setBillingDate(new Date(invoice.getCreated() * 1000L));
            Date dueDate = invoice.getDueDate() != null
                    ? new Date(invoice.getDueDate() * 1000L)
                    : new Date((invoice.getCreated() + (7 * 24 * 60 * 60)) * 1000L); // fallback: created + 7 days
            existingInvoice.setDueDate(dueDate);
            existingInvoice.setPeriodStart(new Date(invoice.getPeriodStart() * 1000L));
            existingInvoice.setPeriodEnd(new Date(invoice.getPeriodEnd() * 1000L));
            invoiceRepository.save(existingInvoice);

            prePaymentService.updateInvoiceId(invoice.getId(), companyId);
        }
    }

    private void updateCourseInstallmentPayment(String userId, String courseId, String installmentId) throws StripeException {
        empCoursesService.updateInstallmentPayment(userId, courseId, installmentId, "paid");
    }

    private void updateFullCoursePayment(String userId, String courseId, String installmentId) throws StripeException {
        empCoursesService.updateFullCoursePayment(userId, courseId, installmentId, "paid");
    }

    private void createSubscription(String companyId, String planName, Session subscriptionSession) throws StripeException {
        Subscription subscription = Subscription.retrieve(subscriptionSession.getSubscription());

        SubscriptionsModel subscriptionsModel = new SubscriptionsModel();
        subscriptionsModel.setCompanyId(companyId);
        subscriptionsModel.setSubscriptionId(subscription.getId());
        subscriptionsModel.setPlan_name(planName);
        subscriptionsModel.setCost(String.valueOf(subscription.getItems().getData().get(0).getPlan().getAmount() / 100.0));
        subscriptionsModel.setBilling_cycle(subscription.getBillingCycleAnchor().toString());
        subscriptionsModel.setStart_date(subscription.getCurrentPeriodStart().toString());
        subscriptionsModel.setEnd_date(subscription.getCurrentPeriodEnd().toString());
        subscriptionsModel.set_active(true);
        subscriptionService.updateSubscription(companyId, subscriptionsModel);
    }

    private void createBillingHistory(String companyId, String invoiceId, Session session, String productType) throws StripeException {
        PaymentIntent paymentIntent = new PaymentIntent();
        if (session.getPaymentIntent() == null && session.getMode().equals("subscription")) {
            Subscription subscription = Subscription.retrieve(session.getSubscription());
            String latestInvoiceId = subscription.getLatestInvoice();

            if (latestInvoiceId != null) {
                Invoice latestInvoice = Invoice.retrieve(latestInvoiceId);
                String paymentIntentId = latestInvoice.getPaymentIntent();

                if (paymentIntentId != null) {
                    paymentIntent = PaymentIntent.retrieve(paymentIntentId);
                } else {
                    auditLogService.markFailed(invoiceId, "PaymentIntent is null in latest invoice", true);
                }
            } else {
                auditLogService.markFailed(invoiceId, "Subscription does not contain latest invoice", true);
            }
        } else {
            paymentIntent = PaymentIntent.retrieve(session.getPaymentIntent());
        }

        if (paymentIntent == null) {
            auditLogService.markFailed(invoiceId, "PaymentIntent is null", true);
            return;
        }

        BillingHistoryModel billingHistory = new BillingHistoryModel();
        if ("course".equals(productType)) {
            billingHistory.setUserId(companyId);
        } else if ("subscription".equals(productType)) {
            billingHistory.setCompanyId(companyId);
        }
        billingHistory.setAmount(String.valueOf(paymentIntent.getAmountReceived()));
        billingHistory.setDate(new Date().toString());
        billingHistory.setInvoice_id(invoiceId);
        billingHistory.setStatus("Completed");
        billingHistoryService.save(billingHistory);
    }

    private void createPaymentMethod(String companyId, Session session, String productType) throws StripeException {
        PaymentIntent paymentIntent = new PaymentIntent();
        if (session.getPaymentIntent() == null && session.getMode().equals("subscription")) {
            Subscription subscription = Subscription.retrieve(session.getSubscription());
            String latestInvoiceId = subscription.getLatestInvoice();

            if (latestInvoiceId != null) {
                Invoice latestInvoice = Invoice.retrieve(latestInvoiceId);
                String paymentIntentId = latestInvoice.getPaymentIntent();

                if (paymentIntentId != null) {
                    paymentIntent = PaymentIntent.retrieve(paymentIntentId);
                } else {
                    auditLogService.markFailed("unknown", "PaymentIntent is null in latest invoice", true);
                }
            } else {
                auditLogService.markFailed("unknown", "Subscription does not contain latest invoice", true);
            }
        } else {
            paymentIntent = PaymentIntent.retrieve(session.getPaymentIntent());
        }

        if (paymentIntent == null) {
            auditLogService.markFailed("unknown", "PaymentIntent is null", true);
            return;
        }

        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentIntent.getPaymentMethod());

        PaymentMethodsModel paymentMethodModel = new PaymentMethodsModel();
        if ("course".equals(productType)) {
            paymentMethodModel.setUserId(companyId);
        } else if ("subscription".equals(productType)) {
            paymentMethodModel.setCompanyId(companyId);
        }
        paymentMethodModel.setType(paymentMethod.getType());
        paymentMethodModel.setLast_four(paymentMethod.getCard().getLast4());
        paymentMethodModel.setExpiry_date(paymentMethod.getCard().getExpMonth() + "/" + paymentMethod.getCard().getExpYear());
        paymentMethodService.save(paymentMethodModel);
    }

    private void updateCompanyStatus(String companyId, Session session) throws StripeException {
        PaymentIntent paymentIntent = new PaymentIntent();
        if (session.getPaymentIntent() == null && session.getMode().equals("subscription")) {
            Subscription subscription = Subscription.retrieve(session.getSubscription());
            String latestInvoiceId = subscription.getLatestInvoice();

            if (latestInvoiceId != null) {
                Invoice latestInvoice = Invoice.retrieve(latestInvoiceId);
                String paymentIntentId = latestInvoice.getPaymentIntent();

                if (paymentIntentId != null) {
                    paymentIntent = PaymentIntent.retrieve(paymentIntentId);
                } else {
                    auditLogService.markFailed("unknown", "PaymentIntent is null in latest invoice", true);
                }
            } else {
                auditLogService.markFailed("unknown", "Subscription does not contain latest invoice", true);
            }
        } else {
            paymentIntent = PaymentIntent.retrieve(session.getPaymentIntent());
        }

        if (paymentIntent == null) {
            auditLogService.markFailed("unknown", "PaymentIntent is null", true);
            return;
        }
        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentIntent.getPaymentMethod());
        Subscription subscription = Subscription.retrieve(session.getSubscription());

        companyService.findAndUpdateCompanyLevel(companyId, "3");
        cmpPostedJobsService.findAndUpdateCompanyLevel(companyId, "3");
        credentialsService.findAndUpdateCompanyLevel(companyId, "3");

        prePaymentService.updateSubscriptionId(subscription.getId(), companyId);
        prePaymentService.updatePaymentMethodId(paymentMethod.getId(), companyId);
        prePaymentService.updateStatus(companyId, "Completed");
    }

    private void handleSubscriptionUpdated(Event event) {
        String companyId;
        Subscription subscription = (Subscription) event.getDataObjectDeserializer().getObject().orElse(null);
        if (subscription == null) {
            auditLogService.markFailed("unknown", "Subscription object is null", true);
            return;
        }

        try {
            companyId = getCompanyIdFromSubscription(subscription);

            SubscriptionsModel subscriptionsModel = new SubscriptionsModel();
            subscriptionsModel.setPlan_name(subscription.getItems().getData().get(0).getPlan().getNickname());
            subscriptionsModel.setCost(String.valueOf(subscription.getItems().getData().get(0).getPlan().getAmount() / 100.0));
            subscriptionsModel.setBilling_cycle(subscription.getBillingCycleAnchor().toString());
            subscriptionsModel.setStart_date(subscription.getCurrentPeriodStart().toString());
            subscriptionsModel.setEnd_date(subscription.getCurrentPeriodEnd().toString());
            subscriptionsModel.set_active(subscription.getStatus().equals("active"));
            subscriptionService.updateSubscription(companyId, subscriptionsModel);

        } catch (StripeException e) {
            auditLogService.markFailed("unknown", e.getMessage(), true);
        }
    }

    String getCompanyIdFromSubscription(Subscription subscription) throws StripeException {
        String companyId = "";
        Customer customer = Customer.retrieve(subscription.getCustomer());
        if (customer.getMetadata() != null && customer.getMetadata().containsKey("company_id")) {
            companyId = customer.getMetadata().get("company_id");
        } else if (subscription.getMetadata() != null && subscription.getMetadata().containsKey("company_id")) {
            companyId = subscription.getMetadata().get("company_id");
        } else {
            auditLogService.markFailed("unknown", "Missing required metadata: company_id", true);
            return companyId;
        }
        return companyId;
    }

    private void handleSubscriptionDeleted(Event event) {
        Subscription subscription = (Subscription) event.getDataObjectDeserializer().getObject().orElse(null);
        if (subscription == null) {
            auditLogService.markFailed("unknown", "Subscription object is null", true);
            return;
        }
        subscriptionService.markAsInactive(subscription.getId());
        auditLogService.logEvent(event, "Subscription deleted for subscriptionId: " + subscription.getId());
    }
}
