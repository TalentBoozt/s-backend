package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.dto.plan.LimitConfig;
import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanConfigService {

    public LimitConfig getPlanLimits(ESubscriptionPlan plan) {
        if (plan == null) {
            plan = ESubscriptionPlan.FREE;
        }

        switch (plan) {
            case PRO:
                return LimitConfig.builder()
                        .maxCourses(Integer.MAX_VALUE)
                        .aiCreditsPerMonth(1000)
                        .maxAiGenerationsPerMonth(10)
                        .validationCreditsPerMonth(0)
                        .commissionRate(0.05)
                        .features(List.of("BASIC_ANALYTICS", "AI_TOOLS", "COURSE_VALIDATION"))
                        .build();
            case PREMIUM:
                return LimitConfig.builder()
                        .maxCourses(Integer.MAX_VALUE)
                        .aiCreditsPerMonth(2000)
                        .maxAiGenerationsPerMonth(10)
                        .validationCreditsPerMonth(10)
                        .commissionRate(0.03)
                        .features(List.of("ADVANCED_ANALYTICS", "AI_TOOLS", "COURSE_VALIDATION", "PRIORITY_SUPPORT",
                                "MARKETING_TOOLS"))
                        .build();
            case ENTERPRISE:
                return LimitConfig.builder()
                        .maxCourses(Integer.MAX_VALUE)
                        .aiCreditsPerMonth(10000)
                        .maxAiGenerationsPerMonth(500)
                        .validationCreditsPerMonth(500)
                        .commissionRate(0.0) // Custom agreements
                        .features(List.of("ADVANCED_ANALYTICS", "AI_TOOLS", "COURSE_VALIDATION", "PRIORITY_SUPPORT",
                                "MARKETING_TOOLS", "WHITE_LABEL", "CUSTOM_DOMAIN", "API_ACCESS"))
                        .build();
            case FREE:
            default:
                return LimitConfig.builder()
                        .maxCourses(1)
                        .aiCreditsPerMonth(0)
                        .maxAiGenerationsPerMonth(0)
                        .validationCreditsPerMonth(0)
                        .commissionRate(0.07)
                        .features(List.of("BASIC_ANALYTICS"))
                        .build();
        }
    }
}
