package com.talentboozt.s_backend.domains.subscription.service;

import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import com.talentboozt.s_backend.domains.subscription.model.FeatureFlag;
import com.talentboozt.s_backend.domains.subscription.repository.mongodb.FeatureFlagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeatureFlagSeeder implements CommandLineRunner {

    private final FeatureFlagRepository repository;

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            log.info("Seeding default feature flags...");
            seedFlags();
        }
    }

    private void seedFlags() {
        // AI Features
        addFlag(ESubscriptionPlan.PRO, "AI_GENERATION", true);
        addFlag(ESubscriptionPlan.PREMIUM, "AI_GENERATION", true);
        addFlag(ESubscriptionPlan.ENTERPRISE, "AI_GENERATION", true);

        addFlag(ESubscriptionPlan.PRO, "AI_VALIDATION", true);
        addFlag(ESubscriptionPlan.PREMIUM, "AI_VALIDATION", true);
        addFlag(ESubscriptionPlan.ENTERPRISE, "AI_VALIDATION", true);

        // Monetization
        addFlag(ESubscriptionPlan.PRO, "COUPONS", true);
        addFlag(ESubscriptionPlan.PREMIUM, "COUPONS", true);
        addFlag(ESubscriptionPlan.ENTERPRISE, "COUPONS", true);

        // Analytics
        addFlag(ESubscriptionPlan.FREE, "BASIC_ANALYTICS", true);
        addFlag(ESubscriptionPlan.PRO, "BASIC_ANALYTICS", true);
        addFlag(ESubscriptionPlan.PREMIUM, "BASIC_ANALYTICS", true);
        addFlag(ESubscriptionPlan.ENTERPRISE, "BASIC_ANALYTICS", true);

        addFlag(ESubscriptionPlan.PREMIUM, "ADVANCED_ANALYTICS", true);
        addFlag(ESubscriptionPlan.ENTERPRISE, "ADVANCED_ANALYTICS", true);

        // Listing
        addFlag(ESubscriptionPlan.PREMIUM, "FEATURED_LISTING", true);
        addFlag(ESubscriptionPlan.ENTERPRISE, "FEATURED_LISTING", true);
        
        // Enterprise special
        addFlag(ESubscriptionPlan.ENTERPRISE, "WHITE_LABEL", true);
        addFlag(ESubscriptionPlan.ENTERPRISE, "API_ACCESS", true);
    }

    private void addFlag(ESubscriptionPlan plan, String key, boolean enabled) {
        repository.save(FeatureFlag.builder()
                .plan(plan)
                .featureKey(key)
                .enabled(enabled)
                .build());
    }
}
