package com.talentboozt.s_backend.domains.payment.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stripe.model.*;
import com.stripe.param.CouponCreateParams;
import com.stripe.param.InvoiceCreateParams;
import com.stripe.param.InvoiceItemCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.talentboozt.s_backend.domains.audit_logs.service.StripeAuditLogService;
import com.talentboozt.s_backend.domains.auth.service.CredentialsService;
import com.talentboozt.s_backend.domains.com_job_portal.service.CmpPostedJobsService;
import com.talentboozt.s_backend.domains.com_job_portal.service.CompanyService;
import com.talentboozt.s_backend.domains.payment.model.BillingHistoryModel;
import com.talentboozt.s_backend.domains.payment.model.InvoicesModel;
import com.talentboozt.s_backend.domains.payment.model.PaymentMethodsModel;
import com.talentboozt.s_backend.domains.payment.model.SubscriptionsModel;
import com.talentboozt.s_backend.domains.payment.repository.InvoiceRepository;
import com.talentboozt.s_backend.domains.plat_courses.model.CourseCouponsModel;
import com.talentboozt.s_backend.domains.plat_courses.service.CourseCouponsService;
import com.talentboozt.s_backend.domains.plat_courses.service.EmpCoursesService;
import com.talentboozt.s_backend.shared.utils.ConfigUtility;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StripeService {

    private final ConfigUtility configUtility;
    private final CourseCouponsService courseCouponsService;
    private final StripeAuditLogService auditLogService;
    private final BillingHistoryService billingHistoryService;
    private final PaymentMethodService paymentMethodService;
    private final InvoiceRepository invoiceRepository;
    private final PrePaymentService prePaymentService;
    private final SubscriptionService subscriptionService;
    private final EmpCoursesService empCoursesService;
    private final CompanyService companyService;
    private final CredentialsService credentialsService;
    private final CmpPostedJobsService cmpPostedJobsService;

    @Autowired
    public StripeService(ConfigUtility configUtility, CourseCouponsService courseCouponsService,
                         BillingHistoryService billingHistoryService, StripeAuditLogService auditLogService,
                         PaymentMethodService paymentMethodService, InvoiceRepository invoiceRepository,
                         PrePaymentService prePaymentService, SubscriptionService subscriptionService,
                         EmpCoursesService empCoursesService, CompanyService companyService,
                         CredentialsService credentialsService, CmpPostedJobsService cmpPostedJobsService) {
        this.configUtility = configUtility;
        this.courseCouponsService = courseCouponsService;
        this.billingHistoryService = billingHistoryService;
        this.auditLogService = auditLogService;
        this.paymentMethodService = paymentMethodService;
        this.invoiceRepository = invoiceRepository;
        this.prePaymentService = prePaymentService;
        this.subscriptionService = subscriptionService;
        this.empCoursesService = empCoursesService;
        this.companyService = companyService;
        this.credentialsService = credentialsService;
        this.cmpPostedJobsService = cmpPostedJobsService;
    }

    public Customer createCustomer(String email, String paymentMethodId) throws StripeException {
        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("payment_method", paymentMethodId);
        params.put("invoice_settings", Map.of("default_payment_method", paymentMethodId));

        return Customer.create(params);
    }

    public Subscription createSubscription(String customerId, String priceId) throws StripeException {
        Map<String, Object> params = new HashMap<>();
        params.put("customer", customerId);
        params.put("items", List.of(Map.of("price", priceId)));
        params.put("expand", List.of("latest_invoice.payment_intent"));

        return Subscription.create(params);
    }

    public Invoice getInvoice(String invoiceId) throws StripeException {
        return Invoice.retrieve(invoiceId);
    }

    public Session createSubscriptionSession(Map<String, Object> data) throws StripeException {
        String companyId = (String) data.get("companyId");
        String planName = (String) data.get("planName");
        String referrer = (String) data.get("referrer");
        String encodedReferrer = URLEncoder.encode(referrer, StandardCharsets.UTF_8);

        Map<String, String> PLAN_PRICE_MAP = Map.of(
                "Basic", getRequiredProperty("STRIPE_TEST_PRICE_ID"),
                "Pro", getRequiredProperty("STRIPE_PRO_PRICE_ID"),
                "Pro-Onetime", getRequiredProperty("STRIPE_PRO_ONETIME_PRICE_ID"),
                "Premium", getRequiredProperty("STRIPE_PREMIUM_PRICE_ID"),
                "Premium-Onetime", getRequiredProperty("STRIPE_PREMIUM_ONETIME_PRICE_ID")
        );

        String priceId = PLAN_PRICE_MAP.get(planName);
        if (priceId == null) throw new IllegalArgumentException("Invalid plan: " + planName);

        boolean isOneTime = planName.endsWith("-Onetime");

        Map<String, Object> metadata = Map.of(
                "purchase_type", "subscription",
                "company_id", companyId,
                "plan_name", planName
        );

        Map<String, Object> params = new HashMap<>();
        params.put("line_items", List.of(Map.of("price", priceId, "quantity", 1)));
        params.put("mode", isOneTime ? "payment" : "subscription");
        params.put("metadata", metadata);
        params.put("success_url", configUtility.getProperty("STRIPE_SUCCESS_URL") + "?referrer=" + encodedReferrer);
        params.put("cancel_url", configUtility.getProperty("STRIPE_CANCEL_URL") + "?referrer=" + encodedReferrer);

        if (!isOneTime) {
            params.put("subscription_data", Map.of("metadata", metadata));
        }

        return Session.create(params);
    }

    public Session createCourseCheckoutSession(Map<String, Object> data, String type) throws StripeException {
        String userId = (String) data.get("userId");
        String courseId = (String) data.get("courseId");
        String installmentId = (String) data.get("installmentId");
        String couponCode = (String) data.get("couponCode");
        String productId = (String) data.get("productId");
        String priceId = (String) data.get("priceId");
        String priceType = (String) data.get("priceType");
        String currency = (String) data.getOrDefault("currency", "usd");
        String referrer = (String) data.get("referrer");
        String encodedReferrer = referrer != null ? URLEncoder.encode(referrer, StandardCharsets.UTF_8) : "";

        // ----- Metadata -----
        Map<String, String> metadata = new HashMap<>();
        metadata.put("purchase_type", type);
        metadata.put("user_id", userId);
        metadata.put("course_id", courseId);
        if (installmentId != null) metadata.put("installment_id", installmentId);
        if (productId != null) metadata.put("product_id", productId);
        if (priceId != null) metadata.put("price_id", priceId);
        if (priceType != null) metadata.put("price_type", priceType);
        if (couponCode != null) metadata.put("coupon_code", couponCode);
        if (referrer != null) metadata.put("referrer", referrer);

        // ----- Detect if this is subscription or one-time -----
        boolean isSubscription = "subscription".equalsIgnoreCase(type);

        SessionCreateParams.Builder builder = SessionCreateParams.builder()
                .setMode(isSubscription ? SessionCreateParams.Mode.SUBSCRIPTION : SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(configUtility.getProperty("STRIPE_SUCCESS_URL") + "?referrer=" + encodedReferrer)
                .setCancelUrl(configUtility.getProperty("STRIPE_CANCEL_URL") + "?referrer=" + encodedReferrer)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPrice(priceId)
                        .build())
                .putAllMetadata(metadata)
                .setCustomerCreation(SessionCreateParams.CustomerCreation.ALWAYS);

        // ----- Handle coupon if provided -----
        if (couponCode != null && !couponCode.isBlank()) {
            try {
                CourseCouponsModel coupon = courseCouponsService.findValidCouponByCode(couponCode, userId, courseId, installmentId);

                if (coupon != null && coupon.getStatus() == CourseCouponsModel.Status.ACTIVE) {
                    String stripeCouponId = coupon.getStripeCouponId();
                    if (stripeCouponId == null) {
                        stripeCouponId = createStripeCoupon(coupon, currency);
                        coupon.setStripeCouponId(stripeCouponId);
                        courseCouponsService.addCourseCoupon(coupon); // persist to DB
                    }

                    builder.addDiscount(SessionCreateParams.Discount.builder()
                            .setCoupon(stripeCouponId)
                            .build());
                }
            } catch (Exception e) {
                auditLogService.logCustom("Invalid Coupon", e.getMessage());
                // continue without coupon, don't block purchase
            }
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        SessionCreateParams params = builder.build();
        auditLogService.logCustom("Checkout Params", gson.toJson(params));

        return Session.create(builder.build());
    }

    private String getRequiredProperty(String key) {
        String value = configUtility.getProperty(key);
        if (value == null) {
            throw new IllegalStateException("Missing Stripe config value for: " + key);
        }
        return value;
    }

    public Product createProduct(String courseName, String courseDescription) throws StripeException {
        Map<String, Object> params = Map.of(
                "name", courseName,
                "description", courseDescription
        );
        return Product.create(params);
    }

    public Price createPriceForCourse(String productId, Long amountInCents, String currency) throws StripeException {
        Map<String, Object> params = Map.of(
                "unit_amount", amountInCents,
                "currency", currency,
                "product", productId
        );
        return Price.create(params);
    }

    public Price createCustomPrice(String productId, Long amountInCents, String currency) throws StripeException {
        Map<String, Object> params = new HashMap<>();
        params.put("unit_amount", amountInCents);
        params.put("currency", currency);
        params.put("product", productId);
        return Price.create(params);
    }

    public void archivePrice(String priceId) throws StripeException {
        Price price = Price.retrieve(priceId);
        Map<String, Object> params = new HashMap<>();
        params.put("active", false);
        price.update(params);
    }

    private String createStripeCoupon(CourseCouponsModel coupon, String currency) throws StripeException {
        CouponCreateParams.Builder builder = CouponCreateParams.builder();

        // ----- Discount type -----
        if ("amount".equalsIgnoreCase(coupon.getDiscountType())) {
            BigDecimal discountValue = new BigDecimal(coupon.getDiscount());
            long amountOff = discountValue.multiply(BigDecimal.valueOf(100)).longValueExact();

            builder.setCurrency(currency.toLowerCase())
                    .setAmountOff(amountOff);

        } else if ("percentage".equalsIgnoreCase(coupon.getDiscountType())) {
            BigDecimal percentOff = new BigDecimal(coupon.getDiscount());
            builder.setPercentOff(percentOff);
        } else {
            throw new IllegalArgumentException("Unsupported discount type: " + coupon.getDiscountType());
        }

        // ----- Duration -----
        if (coupon.getMaxRedemptions() == 1) {
            builder.setDuration(CouponCreateParams.Duration.ONCE);
        } else {
            // If unlimited or multi-use
            builder.setDuration(CouponCreateParams.Duration.FOREVER);
        }

        // ----- Expiry -----
        if (coupon.getExpiresAt() != null) {
            builder.setRedeemBy(coupon.getExpiresAt().getEpochSecond());
        }

        // ----- Max redemptions -----
        if (coupon.getMaxRedemptions() > 0) {
            builder.setMaxRedemptions((long) coupon.getMaxRedemptions());
        }

        Coupon stripeCoupon = Coupon.create(builder.build());
        return stripeCoupon.getId();
    }

    // ------------------- INVOICE HANDLING -------------------

    public void storeInvoice(Invoice invoice, String sessionId) {
        if (invoice == null) return;

        if (invoiceRepository.existsBySessionId(sessionId)) {
            auditLogService.logCustom("invoice", "⚠️ Duplicate invoice ignored for session " + sessionId);
            return;
        }

        try {
            String companyId = extractCompanyId(invoice);

            InvoicesModel invoiceModel = new InvoicesModel();
            invoiceModel.setInvoiceId(invoice.getId());
            invoiceModel.setCompanyId(companyId);
            invoiceModel.setSubscriptionId(invoice.getSubscription());
            invoiceModel.setAmountDue(String.valueOf(invoice.getAmountDue()));
            invoiceModel.setStatus(invoice.getStatus());
            invoiceModel.setBillingDate(new Date(invoice.getCreated() * 1000L));
            Date dueDate = invoice.getDueDate() != null
                    ? new Date(invoice.getDueDate() * 1000L)
                    : new Date((invoice.getCreated() + (7 * 24 * 60 * 60)) * 1000L);
            invoiceModel.setDueDate(dueDate);
            invoiceModel.setPeriodStart(invoice.getPeriodStart() != null ? new Date(invoice.getPeriodStart() * 1000L) : null);
            invoiceModel.setPeriodEnd(invoice.getPeriodEnd() != null ? new Date(invoice.getPeriodEnd() * 1000L) : null);
            invoiceModel.setInvoice_pdf(invoice.getInvoicePdf());
            invoiceModel.setHosted_invoice_url(invoice.getHostedInvoiceUrl());
            invoiceModel.setSessionId(sessionId);

            invoiceRepository.save(invoiceModel);

            if (companyId != null) {
                prePaymentService.updateInvoiceId(invoice.getId(), companyId);
            }

            auditLogService.logCustom("invoice", "✅ Stored invoice: " + invoice.getId());
        } catch (Exception e) {
            auditLogService.markFailed("invoice-store", "❌ Error storing invoice: " + e.getMessage(), true);
        }
    }

    public Invoice createManualInvoiceForOneTime(Session session, String description) throws StripeException {
        if (session.getCustomer() == null || session.getPaymentIntent() == null) {
            throw new IllegalArgumentException("Session must have a customer and payment intent to create invoice");
        }

        String customerId = session.getCustomer();
        PaymentIntent paymentIntent = PaymentIntent.retrieve(session.getPaymentIntent());

        // Create Invoice Item
        InvoiceItem.create(InvoiceItemCreateParams.builder()
                .setCustomer(customerId)
                .setCurrency(paymentIntent.getCurrency())
                .setAmount(paymentIntent.getAmountReceived()) // cents
                .setDescription(description)
                .build());

        // Create and finalize Invoice
        Invoice invoice = Invoice.create(InvoiceCreateParams.builder()
                .setCustomer(customerId)
                .setAutoAdvance(true) // finalize automatically
                .build());

        auditLogService.logCustom("invoice", "🧾 Manual invoice created: " + invoice.getId());
        return invoice;
    }

    public void updateInvoice(Invoice invoice) {
        if (invoice == null) return;

        try {
            String companyId = extractCompanyId(invoice);
            if (companyId == null) {
                auditLogService.markFailed(invoice.getId(), "Missing company_id in metadata", true);
                return;
            }

            InvoicesModel existingInvoice = invoiceRepository.findByInvoiceId(invoice.getId());
            if (existingInvoice != null) {
                existingInvoice.setAmountDue(String.valueOf(invoice.getAmountDue()));
                existingInvoice.setStatus(invoice.getStatus());
                existingInvoice.setBillingDate(new Date(invoice.getCreated() * 1000L));
                Date dueDate = invoice.getDueDate() != null
                        ? new Date(invoice.getDueDate() * 1000L)
                        : new Date((invoice.getCreated() + (7 * 24 * 60 * 60)) * 1000L);
                existingInvoice.setDueDate(dueDate);
                existingInvoice.setPeriodStart(new Date(invoice.getPeriodStart() * 1000L));
                existingInvoice.setPeriodEnd(new Date(invoice.getPeriodEnd() * 1000L));
                invoiceRepository.save(existingInvoice);

                prePaymentService.updateInvoiceId(invoice.getId(), companyId);
            }
        } catch (Exception e) {
            auditLogService.markFailed(invoice.getId(), "❌ Error updating invoice: " + e.getMessage(), true);
        }
    }

    // ------------------- SUBSCRIPTIONS -------------------

    public void createSubscriptionWH(String companyId, String planName, Session subscriptionSession) throws StripeException {
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

    public void updateSubscription(Subscription subscription) {
        if (subscription == null) return;

        try {
            String companyId = getCompanyIdFromSubscription(subscription);
            SubscriptionsModel model = new SubscriptionsModel();
            model.setPlan_name(subscription.getItems().getData().get(0).getPlan().getNickname());
            model.setCost(String.valueOf(subscription.getItems().getData().get(0).getPlan().getAmount() / 100.0));
            model.setBilling_cycle(subscription.getBillingCycleAnchor().toString());
            model.setStart_date(subscription.getCurrentPeriodStart().toString());
            model.setEnd_date(subscription.getCurrentPeriodEnd().toString());
            model.set_active("active".equals(subscription.getStatus()));

            subscriptionService.updateSubscription(companyId, model);
        } catch (Exception e) {
            auditLogService.markFailed("unknown", "❌ Error updating subscription: " + e.getMessage(), true);
        }
    }

    public void deleteSubscription(Subscription subscription) {
        if (subscription == null) return;
        subscriptionService.markAsInactive(subscription.getId());
    }

    // ------------------- BILLING -------------------

    public void createBillingHistory(String entityId, String invoiceId, Session session, String productType) throws StripeException {
        PaymentIntent paymentIntent = safeRetrievePaymentIntent(session);
        if (paymentIntent == null) {
            auditLogService.markFailed(invoiceId, "❌ No PaymentIntent found", true);
            return;
        }

        if (billingHistoryService.existsBySessionId(session.getId())) {
            auditLogService.logCustom("webhook", "⚠️ Duplicate session ignored: " + session.getId());
            return;
        }

        BillingHistoryModel billingHistory = new BillingHistoryModel();
        if ("course".equals(productType)) {
            billingHistory.setUserId(entityId);
        } else if ("subscription".equals(productType)) {
            billingHistory.setCompanyId(entityId);
        }
        billingHistory.setAmount(String.valueOf(paymentIntent.getAmountReceived()));
        billingHistory.setDate(new Date().toString());
        billingHistory.setInvoice_id(invoiceId);
        billingHistory.setSessionId(session.getId());
        billingHistory.setStatus("Completed");

        billingHistoryService.save(billingHistory);
    }

    public void createPaymentMethod(String entityId, Session session, String productType) throws StripeException {
        PaymentIntent paymentIntent = safeRetrievePaymentIntent(session);
        if (paymentIntent == null) {
            auditLogService.markFailed("unknown", "❌ No PaymentIntent found", true);
            return;
        }

        if (paymentMethodService.existsBySessionId(session.getId())) {
            auditLogService.logCustom("webhook", "⚠️ Duplicate payment method ignored: " + paymentIntent.getPaymentMethod());
            return;
        }

        PaymentMethod pm = PaymentMethod.retrieve(paymentIntent.getPaymentMethod());

        PaymentMethodsModel pmModel = new PaymentMethodsModel();
        if ("course".equals(productType)) {
            pmModel.setUserId(entityId);
        } else if ("subscription".equals(productType)) {
            pmModel.setCompanyId(entityId);
        }
        pmModel.setType(pm.getType());
        pmModel.setLast_four(pm.getCard().getLast4());
        pmModel.setSessionId(session.getId());
        pmModel.setExpiry_date(pm.getCard().getExpMonth() + "/" + pm.getCard().getExpYear());

        paymentMethodService.save(pmModel);
    }

    // ------------------- COURSES UPDATE -------------------

    public void updateCourseInstallmentPayment(String userId, String courseId, String installmentId) {
        try {
            empCoursesService.updateInstallmentPayment(userId, courseId, installmentId, "paid");
        } catch (Exception e) {
            auditLogService.logCustom("installment payment", "❌ Error updating installment payment for "+userId+" - "+courseId+" - "+installmentId+": " + e.getMessage());
        }
    }

    public void updateFullCoursePayment(String userId, String courseId, String installmentId) {
        try {
            empCoursesService.updateFullCoursePayment(userId, courseId, installmentId, "paid");
        } catch (Exception e) {
            auditLogService.logCustom("full course payment", "❌ Error updating full course payment for "+userId+" - "+courseId+" - "+installmentId+": " + e.getMessage());
        }
    }

    // ------------------- COMPANY UPDATE JOB BOARD -------------------

    public void updateCompanyStatus(String companyId, Session session) throws StripeException {
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

    // ------------------- HELPERS -------------------

    private PaymentIntent safeRetrievePaymentIntent(Session session) throws StripeException {
        if (session.getPaymentIntent() != null) {
            return PaymentIntent.retrieve(session.getPaymentIntent());
        }

        if ("subscription".equals(session.getMode())) {
            Subscription subscription = Subscription.retrieve(session.getSubscription());
            String latestInvoiceId = subscription.getLatestInvoice();
            if (latestInvoiceId != null) {
                Invoice latestInvoice = Invoice.retrieve(latestInvoiceId);
                if (latestInvoice.getPaymentIntent() != null) {
                    return PaymentIntent.retrieve(latestInvoice.getPaymentIntent());
                }
            }
        }
        return null;
    }

    private String extractCompanyId(Invoice invoice) throws StripeException {
        if (invoice.getCustomer() != null) {
            Customer customer = Customer.retrieve(invoice.getCustomer());
            if (customer.getMetadata() != null && customer.getMetadata().containsKey("company_id")) {
                return customer.getMetadata().get("company_id");
            }
        }

        if (!invoice.getLines().getData().isEmpty()
                && invoice.getLines().getData().get(0).getMetadata().containsKey("company_id")) {
            return invoice.getLines().getData().get(0).getMetadata().get("company_id");
        }

        return null; // for one-time user payments, we don’t always have companyId
    }

    private String getCompanyIdFromSubscription(Subscription subscription) throws StripeException {
        Customer customer = Customer.retrieve(subscription.getCustomer());
        if (customer.getMetadata() != null && customer.getMetadata().containsKey("company_id")) {
            return customer.getMetadata().get("company_id");
        } else if (subscription.getMetadata() != null && subscription.getMetadata().containsKey("company_id")) {
            return subscription.getMetadata().get("company_id");
        }
        return null;
    }
}

