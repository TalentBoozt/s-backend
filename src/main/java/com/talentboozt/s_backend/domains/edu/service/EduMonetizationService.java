package com.talentboozt.s_backend.domains.edu.service;

import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;

import com.talentboozt.s_backend.domains.edu.dto.monetization.CheckoutRequest;
import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import com.talentboozt.s_backend.domains.edu.exception.EduBadRequestException;
import com.talentboozt.s_backend.domains.edu.model.ESubscriptions;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ESubscriptionsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EduMonetizationService {

    private final ESubscriptionsRepository subscriptionsRepository;
    private final EduSubscriptionService subscriptionService;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Value("${stripe.edu.price.pro.monthly:}")
    private String stripePriceProMonthly;
    @Value("${stripe.edu.price.pro.yearly:}")
    private String stripePriceProYearly;
    @Value("${stripe.edu.price.premium.monthly:}")
    private String stripePricePremiumMonthly;
    @Value("${stripe.edu.price.premium.yearly:}")
    private String stripePricePremiumYearly;
    @Value("${stripe.edu.price.free:}")
    private String stripePriceFree;

    public EduMonetizationService(ESubscriptionsRepository subscriptionsRepository, 
                                  EduSubscriptionService subscriptionService) {
        this.subscriptionsRepository = subscriptionsRepository;
        this.subscriptionService = subscriptionService;
    }

    public Map<String, String> createCheckoutSession(CheckoutRequest request) throws Exception {
        ESubscriptions subscription = subscriptionService.getUserSubscription(request.getUserId());
        
        // Ensure Stripe Customer ID exists
        if (subscription.getStripeCustomerId() == null) {
            CustomerCreateParams params = CustomerCreateParams.builder()
                    .setMetadata(Map.of("userId", request.getUserId()))
                    .build();
            Customer customer = Customer.create(params);
            subscription.setStripeCustomerId(customer.getId());
            subscriptionsRepository.save(subscription);
        }

        // Price mapping (mock for now)
        String priceId = getPriceIdForPlan(request.getPlanName(), request.getBillingCycle());
        subscription.setStripePriceId(priceId);
        subscriptionsRepository.save(subscription);

        // Generate idempotency key to prevent duplicate subscriptions on network retries
        String idempotencyKey = UUID.randomUUID().toString();

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setCustomer(subscription.getStripeCustomerId())
                .setSuccessUrl(frontendUrl + "/learner/subscription/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(frontendUrl + "/pricing")
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPrice(priceId)
                        .build())
                .build();

        RequestOptions requestOptions = RequestOptions.builder()
                .setIdempotencyKey(idempotencyKey)
                .build();
        Session session = Session.create(params, requestOptions);
        return Map.of("url", session.getUrl());
    }

    public Map<String, String> createPortalSession(String userId) throws Exception {
        ESubscriptions subscription = subscriptionService.getUserSubscription(userId);
        if (subscription.getStripeCustomerId() == null) {
            throw new EduBadRequestException("No Stripe customer found for user to manage billing portal.");
        }

        com.stripe.param.billingportal.SessionCreateParams params = 
            com.stripe.param.billingportal.SessionCreateParams.builder()
                .setCustomer(subscription.getStripeCustomerId())
                .setReturnUrl(frontendUrl + "/learner/profile")
                .build();

        RequestOptions requestOptions = RequestOptions.builder()
                .setIdempotencyKey(UUID.randomUUID().toString())
                .build();
        com.stripe.model.billingportal.Session portalSession = com.stripe.model.billingportal.Session.create(params, requestOptions);
        return Map.of("url", portalSession.getUrl());
    }

    private String getPriceIdForPlan(String planName, String cycle) {
        boolean monthly = "monthly".equalsIgnoreCase(cycle);
        if ("PRO".equalsIgnoreCase(planName)) {
            String id = monthly ? stripePriceProMonthly : stripePriceProYearly;
            return requireStripePriceId(id, "PRO", monthly);
        }
        if ("PREMIUM".equalsIgnoreCase(planName)) {
            String id = monthly ? stripePricePremiumMonthly : stripePricePremiumYearly;
            return requireStripePriceId(id, "PREMIUM", monthly);
        }
        if (stripePriceFree != null && !stripePriceFree.isBlank()) {
            return stripePriceFree;
        }
        throw new IllegalStateException(
                "Subscription checkout requires stripe.edu.price.free or a paid plan (PRO/PREMIUM) with Stripe Price IDs configured.");
    }

    private static String requireStripePriceId(String id, String plan, boolean monthly) {
        if (id == null || id.isBlank()) {
            throw new IllegalStateException(
                    "Stripe Price ID not configured for " + plan + " (" + (monthly ? "monthly" : "yearly")
                            + "). Set stripe.edu.price." + plan.toLowerCase() + "." + (monthly ? "monthly" : "yearly")
                            + " in application.properties or environment variables.");
        }
        return id.trim();
    }

    public ESubscriptions getSubscriptionStatus(String userId) {
        return subscriptionService.getUserSubscription(userId);
    }
}
