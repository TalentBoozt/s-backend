package com.talentboozt.s_backend.Service.common.payment;

import com.stripe.model.Price;
import com.talentboozt.s_backend.Utils.ConfigUtility;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Invoice;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Session createCheckoutSession(String companyId, String planName) throws StripeException {
        Map<String, String> PLAN_PRICE_MAP = new HashMap<>();
        PLAN_PRICE_MAP.put("Basic", getRequiredProperty("STRIPE_TEST_PRICE_ID"));
        PLAN_PRICE_MAP.put("Pro", getRequiredProperty("STRIPE_PRO_PRICE_ID"));
        PLAN_PRICE_MAP.put("Pro-Onetime", getRequiredProperty("STRIPE_PRO_ONETIME_PRICE_ID"));
        PLAN_PRICE_MAP.put("Premium", getRequiredProperty("STRIPE_PREMIUM_PRICE_ID"));
        PLAN_PRICE_MAP.put("Premium-Onetime", getRequiredProperty("STRIPE_PREMIUM_ONETIME_PRICE_ID"));

        String priceId = PLAN_PRICE_MAP.get(planName);
        if (priceId == null) {
            throw new IllegalArgumentException("Invalid plan name: " + planName);
        }

        boolean isOneTimePayment = planName.endsWith("-Onetime");
        String sessionMode = isOneTimePayment ? "payment" : "subscription";

        Map<String, Object> metadata = Map.of(
                "company_id", companyId,
                "plan_name", planName
        );

        Map<String, Object> subscriptionData = new HashMap<>();
        subscriptionData.put("metadata", metadata);

        Map<String, Object> params = new HashMap<>();
        params.put("line_items", List.of(Map.of("price", priceId, "quantity", 1)));
        params.put("mode", sessionMode);
        params.put("subscription_data", subscriptionData);
        params.put("success_url", configUtility.getProperty("STRIPE_SUCCESS_URL"));
        params.put("cancel_url", configUtility.getProperty("STRIPE_CANCEL_URL"));
        params.put("metadata", metadata);

        try {
            return Session.create(params);
        } catch (InvalidRequestException e) {
            throw new RuntimeException("Stripe error: " + e.getMessage(), e);
        } catch (StripeException e) {
            throw new RuntimeException("Unexpected Stripe error occurred: ", e);
        }
    }

    private String getRequiredProperty(String key) {
        String value = configUtility.getProperty(key);
        if (value == null) {
            throw new IllegalStateException("Missing Stripe config value for: " + key);
        }
        return value;
    }

    public Price createCustomPrice(String productId, Long amountInCents, String currency) throws StripeException {
        Map<String, Object> params = new HashMap<>();
        params.put("unit_amount", amountInCents);
        params.put("currency", currency);
        params.put("product", productId);
        return Price.create(params);
    }
}

