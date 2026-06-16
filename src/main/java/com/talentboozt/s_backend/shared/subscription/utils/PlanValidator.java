package com.talentboozt.s_backend.shared.subscription.utils;

import com.talentboozt.s_backend.shared.subscription.domain.model.SubscriptionPlanCode;
import com.talentboozt.s_backend.shared.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlanValidator {

    private final SubscriptionService subscriptionService;

    public void validatePlan(String userId, SubscriptionPlanCode requiredPlan) {
        if (!subscriptionService.validateUserPlan(userId, requiredPlan)) {
            throw new RuntimeException("Insufficient plan level. Required: " + requiredPlan);
        }
    }
}
