package com.talentboozt.s_backend.domains.subscription.service;

import com.talentboozt.s_backend.domains.subscription.application.port.PlanCatalogPort;
import com.talentboozt.s_backend.domains.subscription.application.port.UserSubscriptionPort;
import com.talentboozt.s_backend.domains.subscription.domain.model.SubscriptionPlanCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionFeatureFlagService {

    private final PlanCatalogPort planCatalogPort;
    private final UserSubscriptionPort userSubscriptionPort;

    /**
     * Checks if a feature is enabled for a specific user based on their subscription plan.
     */
    @Cacheable(value = "featureFlags", key = "#userId + '_' + #featureKey")
    public boolean isFeatureEnabled(String userId, String featureKey) {
        SubscriptionPlanCode plan = userSubscriptionPort.resolvePlanCodeFromUserProfile(userId);
        return isFeatureEnabledForPlan(plan, featureKey);
    }

    /**
     * Internal check for plan-based feature enabling.
     */
    public boolean isFeatureEnabledForPlan(SubscriptionPlanCode plan, String featureKey) {
        return getFeaturesForPlan(plan).contains(featureKey);
    }

    /**
     * Retrieves all enabled feature keys for a specific plan.
     */
    @Cacheable(value = "planFeatures", key = "#plan")
    public List<String> getFeaturesForPlan(SubscriptionPlanCode plan) {
        SubscriptionPlanCode effective = plan != null ? plan : SubscriptionPlanCode.FREE;
        return planCatalogPort.getPlanLimits(effective).features();
    }
}
