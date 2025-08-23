package com.talentboozt.s_backend.domains.payment.service;

import com.stripe.model.*;
import com.stripe.param.CouponCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.talentboozt.s_backend.domains.audit_logs.service.StripeAuditLogService;
import com.talentboozt.s_backend.domains.plat_courses.cfg.CouponValidationException;
import com.talentboozt.s_backend.domains.plat_courses.model.CourseCouponsModel;
import com.talentboozt.s_backend.domains.plat_courses.service.CourseCouponsService;
import com.talentboozt.s_backend.shared.utils.ConfigUtility;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StripeService {

    @Autowired
    ConfigUtility configUtility;

    @Autowired
    CourseCouponsService courseCouponsService;

    @Autowired
    StripeAuditLogService stripeAuditLogService;

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
        String couponCode = (String) data.get("couponCode"); // optional
        String productId = (String) data.get("productId");
        String priceId = (String) data.get("priceId"); // already saved from course creation
        String priceType = (String) data.get("priceType"); // e.g. "default", "discounted"
        String currency = (String) data.get("currency");
        String referrer = (String) data.get("referrer");
        String encodedReferrer = URLEncoder.encode(referrer, StandardCharsets.UTF_8);

        CourseCouponsModel coupon = null;

        Map<String, String> metadata = new HashMap<>();
        metadata.put("purchase_type", type);
        metadata.put("user_id", userId);
        metadata.put("course_id", courseId);
        metadata.put("installment_id", installmentId);
        metadata.put("product_id", productId);
        metadata.put("price_id", priceId);
        metadata.put("price_type", priceType);
        if (couponCode != null) {
            metadata.put("coupon_code", couponCode);
        }

        SessionCreateParams.Builder builder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(configUtility.getProperty("STRIPE_SUCCESS_URL") + "?referrer=" + encodedReferrer)
                .setCancelUrl(configUtility.getProperty("STRIPE_CANCEL_URL") + "?referrer=" + encodedReferrer)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPrice(priceId)
                                .build()
                )
                .putAllMetadata(metadata);

        if (couponCode != null && !couponCode.isBlank()) {
            try {
                coupon = courseCouponsService.findValidCouponByCode(couponCode, userId, courseId, installmentId);

                if (coupon != null && coupon.getStatus() == CourseCouponsModel.Status.ACTIVE) {
                    String stripeCouponId = createStripeCoupon(coupon, currency != null ? currency : "usd");

                    builder.addDiscount(SessionCreateParams.Discount.builder()
                            .setCoupon(stripeCouponId)
                            .build());
                }
            } catch (Exception e) {
                stripeAuditLogService.logCustom("Invalid Coupon", e.getMessage());
                throw new CouponValidationException("Invalid coupon code.", "COUPON_INVALID");
            }
        }

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
        CouponCreateParams params;

        if ("amount".equalsIgnoreCase(coupon.getDiscountType())) {
            long amountOff = Long.parseLong(coupon.getDiscount());

            params = CouponCreateParams.builder()
                    .setCurrency(currency.toLowerCase()) // Dynamic currency
                    .setAmountOff(amountOff)
                    .setDuration(CouponCreateParams.Duration.ONCE)
                    .build();
        } else {
            double percentOff = Double.parseDouble(coupon.getDiscount());

            params = CouponCreateParams.builder()
                    .setPercentOff(BigDecimal.valueOf(percentOff))
                    .setDuration(CouponCreateParams.Duration.ONCE)
                    .build();
        }

        Coupon stripeCoupon = Coupon.create(params);
        return stripeCoupon.getId();
    }
}

