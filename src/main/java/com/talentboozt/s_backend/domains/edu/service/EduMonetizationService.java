package com.talentboozt.s_backend.domains.edu.service;

import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;

import com.talentboozt.s_backend.domains.edu.dto.monetization.CheckoutRequest;
import com.talentboozt.s_backend.domains.edu.model.ESubscriptions;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ESubscriptionsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EduMonetizationService {

    private final ESubscriptionsRepository subscriptionsRepository;
    private final EduSubscriptionService subscriptionService;

    @Value("${eduapp.frontend.url:http://localhost:4200}")
    private String frontendUrl;

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

        Session session = Session.create(params);
        return Map.of("url", session.getUrl());
    }

    public Map<String, String> createPortalSession(String userId) throws Exception {
        ESubscriptions subscription = subscriptionService.getUserSubscription(userId);
        if (subscription.getStripeCustomerId() == null) {
            throw new RuntimeException("No Stripe customer found for user");
        }

        com.stripe.param.billingportal.SessionCreateParams params = 
            com.stripe.param.billingportal.SessionCreateParams.builder()
                .setCustomer(subscription.getStripeCustomerId())
                .setReturnUrl(frontendUrl + "/learner/profile")
                .build();

        com.stripe.model.billingportal.Session portalSession = com.stripe.model.billingportal.Session.create(params);
        return Map.of("url", portalSession.getUrl());
    }

    private String getPriceIdForPlan(String planName, String cycle) {
        // These should be in environment variables or configuration properties
        if ("PRO".equalsIgnoreCase(planName)) {
            return "monthly".equalsIgnoreCase(cycle) ? "price_pro_monthly" : "price_pro_yearly";
        } else if ("PREMIUM".equalsIgnoreCase(planName)) {
            return "monthly".equalsIgnoreCase(cycle) ? "price_premium_monthly" : "price_premium_yearly";
        }
        return "price_free";
    }

    public ESubscriptions getSubscriptionStatus(String userId) {
        return subscriptionService.getUserSubscription(userId);
    }
}
