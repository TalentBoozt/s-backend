package com.talentboozt.s_backend.domains.subscription.service;

import com.talentboozt.s_backend.domains.edu.service.PlanConfigService;
import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import com.talentboozt.s_backend.domains.edu.model.EUser;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeatureFlagService {

    private final PlanConfigService planConfigService;
    private final EUserRepository userRepository;

    /**
     * Checks if a feature is enabled for a specific user based on their subscription plan.
     */
    @Cacheable(value = "featureFlags", key = "#userId + '_' + #featureKey")
    public boolean isFeatureEnabled(String userId, String featureKey) {
        ESubscriptionPlan plan = ESubscriptionPlan.FREE;
        
        if (userId != null) {
            plan = userRepository.findById(userId)
                    .map(EUser::getPlan)
                    .orElse(ESubscriptionPlan.FREE);
        }

        return isFeatureEnabledForPlan(plan, featureKey);
    }

    /**
     * Internal check for plan-based feature enabling.
     */
    public boolean isFeatureEnabledForPlan(ESubscriptionPlan plan, String featureKey) {
        return getFeaturesForPlan(plan).contains(featureKey);
    }

    /**
     * Retrieves all enabled feature keys for a specific plan.
     */
    @Cacheable(value = "planFeatures", key = "#plan")
    public List<String> getFeaturesForPlan(ESubscriptionPlan plan) {
        return planConfigService.getPlanLimits(plan).getFeatures();
    }
}
