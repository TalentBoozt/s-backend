package com.talentboozt.s_backend.domains.subscription.service;

import com.talentboozt.s_backend.domains.subscription.domain.model.SubscriptionPlanCode;
import com.talentboozt.s_backend.domains.subscription.model.FeatureFlag;
import com.talentboozt.s_backend.domains.subscription.repository.mongodb.SubscriptionFeatureFlagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeatureFlagSeeder implements CommandLineRunner {

    private final SubscriptionFeatureFlagRepository repository;

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            log.info("Seeding default feature flags...");
            seedFlags();
        }
    }

    private void seedFlags() {
        addFlag(SubscriptionPlanCode.PRO, "AI_GENERATION", true);
        addFlag(SubscriptionPlanCode.PREMIUM, "AI_GENERATION", true);
        addFlag(SubscriptionPlanCode.ENTERPRISE, "AI_GENERATION", true);

        addFlag(SubscriptionPlanCode.PRO, "AI_VALIDATION", true);
        addFlag(SubscriptionPlanCode.PREMIUM, "AI_VALIDATION", true);
        addFlag(SubscriptionPlanCode.ENTERPRISE, "AI_VALIDATION", true);

        addFlag(SubscriptionPlanCode.PRO, "COUPONS", true);
        addFlag(SubscriptionPlanCode.PREMIUM, "COUPONS", true);
        addFlag(SubscriptionPlanCode.ENTERPRISE, "COUPONS", true);

        addFlag(SubscriptionPlanCode.FREE, "BASIC_ANALYTICS", true);
        addFlag(SubscriptionPlanCode.PRO, "BASIC_ANALYTICS", true);
        addFlag(SubscriptionPlanCode.PREMIUM, "BASIC_ANALYTICS", true);
        addFlag(SubscriptionPlanCode.ENTERPRISE, "BASIC_ANALYTICS", true);

        addFlag(SubscriptionPlanCode.PREMIUM, "ADVANCED_ANALYTICS", true);
        addFlag(SubscriptionPlanCode.ENTERPRISE, "ADVANCED_ANALYTICS", true);

        addFlag(SubscriptionPlanCode.PREMIUM, "FEATURED_LISTING", true);
        addFlag(SubscriptionPlanCode.ENTERPRISE, "FEATURED_LISTING", true);

        addFlag(SubscriptionPlanCode.ENTERPRISE, "WHITE_LABEL", true);
        addFlag(SubscriptionPlanCode.ENTERPRISE, "API_ACCESS", true);
    }

    private void addFlag(SubscriptionPlanCode plan, String key, boolean enabled) {
        repository.save(FeatureFlag.builder()
                .plan(plan)
                .featureKey(key)
                .enabled(enabled)
                .build());
    }
}
