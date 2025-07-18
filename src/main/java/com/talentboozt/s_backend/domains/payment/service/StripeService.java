package com.talentboozt.s_backend.domains.payment.service;

import com.stripe.model.*;
import com.talentboozt.s_backend.shared.utils.ConfigUtility;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StripeService {

    @Autowired
    ConfigUtility configUtility;

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
        String referrer = (String) data.get("referrer");
        String encodedReferrer = URLEncoder.encode(referrer, StandardCharsets.UTF_8);

        Map<String, Object> metadata = Map.of(
                "purchase_type", type,
                "user_id", userId,
                "course_id", courseId,
                "installment_id", installmentId,
                "product_id", productId,
                "price_id", priceId,
                "price_type", priceType,
                "coupon_code", couponCode != null ? couponCode : ""
        );

        Map<String, Object> params = new HashMap<>();
        params.put("line_items", List.of(Map.of("price", priceId, "quantity", 1)));
        params.put("mode", "payment");
        params.put("metadata", metadata);
        params.put("success_url", configUtility.getProperty("STRIPE_SUCCESS_URL") + "?referrer=" + encodedReferrer);
        params.put("cancel_url", configUtility.getProperty("STRIPE_CANCEL_URL") + "?referrer=" + encodedReferrer);

        return Session.create(params);
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
}

