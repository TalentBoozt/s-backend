package com.talentboozt.s_backend.domains.subscription.infrastructure.external;

import com.talentboozt.s_backend.domains.edu.dto.plan.LimitConfig;
import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import com.talentboozt.s_backend.domains.edu.service.PlanConfigService;
import com.talentboozt.s_backend.domains.subscription.application.dto.PlanLimitsSnapshot;
import com.talentboozt.s_backend.domains.subscription.application.port.PlanCatalogPort;
import com.talentboozt.s_backend.domains.subscription.domain.model.SubscriptionPlanCode;
import com.talentboozt.s_backend.domains.subscription.infrastructure.mapping.LmsPlanAndStatusMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Transitional ACL: maps subscription-owned plan codes to the legacy LMS {@link PlanConfigService}.
 */
@Component
@RequiredArgsConstructor
public class EduPlanCatalogAdapter implements PlanCatalogPort {

    private final PlanConfigService planConfigService;

    @Override
    public PlanLimitsSnapshot getPlanLimits(SubscriptionPlanCode plan) {
        SubscriptionPlanCode effective = plan != null ? plan : SubscriptionPlanCode.FREE;
        ESubscriptionPlan eduPlan = LmsPlanAndStatusMapping.toEduPlan(effective);
        LimitConfig limits = planConfigService.getPlanLimits(eduPlan);
        return new PlanLimitsSnapshot(
                limits.getMaxCourses(),
                limits.getAiCreditsPerMonth(),
                limits.getMaxAiGenerationsPerMonth(),
                limits.getValidationCreditsPerMonth(),
                limits.getHourlyAiLimit(),
                limits.getDailyAiLimit(),
                limits.getCommissionRate(),
                limits.getFeatures() != null ? limits.getFeatures() : java.util.List.of());
    }
}
