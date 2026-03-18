package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionStatus;
import com.talentboozt.s_backend.domains.edu.model.ESubscriptions;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ESubscriptionsRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class EduSubscriptionService {

    private final ESubscriptionsRepository subscriptionsRepository;

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
}
