package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionStatus;
import com.talentboozt.s_backend.domains.edu.model.ESubscriptions;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ESubscriptionsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class EduSubscriptionService {

    private final ESubscriptionsRepository subscriptionsRepository;

    @Value("${stripe.edu.price.pro.monthly:}")
    private String stripePriceProMonthly;
    @Value("${stripe.edu.price.pro.yearly:}")
    private String stripePriceProYearly;
    @Value("${stripe.edu.price.premium.monthly:}")
    private String stripePricePremiumMonthly;
    @Value("${stripe.edu.price.premium.yearly:}")
    private String stripePricePremiumYearly;

    public EduSubscriptionService(ESubscriptionsRepository subscriptionsRepository) {
        this.subscriptionsRepository = subscriptionsRepository;
    }

    public ESubscriptions getUserSubscription(String userId) {
        return subscriptionsRepository.findByUserId(userId)
                .orElseGet(() -> assignDefaultFreePlan(userId));
    }

    public ESubscriptions assignDefaultFreePlan(String userId) {
        ESubscriptions freePlan = ESubscriptions.builder()
                .userId(userId)
                .plan(ESubscriptionPlan.FREE)
                .status(ESubscriptionStatus.ACTIVE)
                .remainingCredits(0)
                .totalCredits(0)
                .price(0.0)
                .currency("USD")
                .autoRenew(false)
                .commissionRate(0.07)
                .maxCourses(1)
                .startDate(Instant.now())
                .features(new String[] { "Basic Access" })
                .build();
        return subscriptionsRepository.save(freePlan);
    }

    public ESubscriptions upgradePlan(String userId, ESubscriptionPlan newPlan) {
        ESubscriptions current = getUserSubscription(userId);

        current.setPlan(newPlan);
        current.setStatus(ESubscriptionStatus.ACTIVE);
        current.setStartDate(Instant.now());

        if (newPlan == ESubscriptionPlan.PRO) {
            current.setPrice(19.99);
            current.setCommissionRate(0.05);
            current.setMaxCourses(Integer.MAX_VALUE);
            current.setRemainingCredits(0);
            current.setTotalCredits(0);
        } else if (newPlan == ESubscriptionPlan.PREMIUM) {
            current.setPrice(49.99);
            current.setCommissionRate(0.02);
            current.setMaxCourses(Integer.MAX_VALUE);
            current.setRemainingCredits(500);
            current.setTotalCredits(500);
        }

        return subscriptionsRepository.save(current);
    }

    public ESubscriptions cancelSubscription(String userId) {
        ESubscriptions current = getUserSubscription(userId);
        current.setStatus(ESubscriptionStatus.CANCELLED);
        current.setAutoRenew(false);
        current.setCancelledAt(Instant.now());
        return subscriptionsRepository.save(current);
    }

    public void updateFromStripeEvent(String stripeCustomerId, String stripeSubscriptionId, String status, String priceId) {
        subscriptionsRepository.findByStripeCustomerId(stripeCustomerId).ifPresent(sub -> {
            sub.setStripeSubscriptionId(stripeSubscriptionId);

            if (priceId != null) {
                sub.setStripePriceId(priceId);
                if (priceId.equals(stripePriceProMonthly) || priceId.equals(stripePriceProYearly)) {
                    sub.setPlan(ESubscriptionPlan.PRO);
                    sub.setPrice(19.99); // Keeping defaults or consider leaving unmodified if already set
                    sub.setCommissionRate(0.05);
                    sub.setMaxCourses(Integer.MAX_VALUE);
                } else if (priceId.equals(stripePricePremiumMonthly) || priceId.equals(stripePricePremiumYearly)) {
                    sub.setPlan(ESubscriptionPlan.PREMIUM);
                    sub.setPrice(49.99);
                    sub.setCommissionRate(0.02);
                    sub.setMaxCourses(Integer.MAX_VALUE);
                    sub.setRemainingCredits(500); // Ideally we only reset this on cycle/creation
                }
            }

            if (status != null) {
                switch (status.toLowerCase()) {
                    case "active":
                        sub.setStatus(ESubscriptionStatus.ACTIVE);
                        break;
                    case "past_due":
                    case "unpaid":
                        sub.setStatus(ESubscriptionStatus.PAST_DUE);
                        break;
                    case "canceled":
                        sub.setStatus(ESubscriptionStatus.CANCELLED);
                        sub.setPlan(ESubscriptionPlan.FREE);
                        sub.setAutoRenew(false);
                        sub.setCancelledAt(Instant.now());
                        break;
                    default:
                        // default leave as is
                }
            }

            subscriptionsRepository.save(sub);
        });
    }

    public void updatePaymentSucceeded(String stripeCustomerId) {
        subscriptionsRepository.findByStripeCustomerId(stripeCustomerId).ifPresent(sub -> {
            sub.setLastPaymentAt(Instant.now());
            sub.setStatus(ESubscriptionStatus.ACTIVE);
            subscriptionsRepository.save(sub);
        });
    }

    public void updatePaymentFailed(String stripeCustomerId) {
        subscriptionsRepository.findByStripeCustomerId(stripeCustomerId).ifPresent(sub -> {
            sub.setStatus(ESubscriptionStatus.PAST_DUE);
            subscriptionsRepository.save(sub);
        });
    }
}
