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
                        .maxCourses(30)
                        .aiCreditsPerMonth(1000)
                        .maxAiGenerationsPerMonth(5)
                        .validationCreditsPerMonth(0)
                        .hourlyAiLimit(5)
                        .dailyAiLimit(20)
                        .commissionRate(0.05)
                        .features(List.of("COUPONS", "BASIC_ANALYTICS", "AI_GENERATION", "COURSE_BUNDLES"))
                        .build();
            case PREMIUM:
                return LimitConfig.builder()
                        .maxCourses(Integer.MAX_VALUE)
                        .aiCreditsPerMonth(5000)
                        .maxAiGenerationsPerMonth(10)
                        .validationCreditsPerMonth(10)
                        .hourlyAiLimit(10)
                        .dailyAiLimit(50)
                        .commissionRate(0.03)
                        .features(List.of("COUPONS", "ADVANCED_ANALYTICS", "AI_GENERATION", "COURSE_VALIDATION", "PRIORITY_SUPPORT", "MARKETING_TOOLS", "COURSE_BUNDLES"))
                        .build();
            case ENTERPRISE:
                return LimitConfig.builder()
                        .maxCourses(Integer.MAX_VALUE)
                        .aiCreditsPerMonth(150000)
                        .maxAiGenerationsPerMonth(250)
                        .validationCreditsPerMonth(500)
                        .hourlyAiLimit(200)
                        .dailyAiLimit(2000)
                        .commissionRate(0.01)
                        .features(List.of("COUPONS", "ADVANCED_ANALYTICS", "AI_GENERATION", "COURSE_VALIDATION", "PRIORITY_SUPPORT", "MARKETING_TOOLS", "WHITE_LABEL", "CUSTOM_DOMAIN", "API_ACCESS", "WORKSPACE", "COURSE_BUNDLES"))
                        .build();
            case FREE:
            default:
                return LimitConfig.builder()
                        .maxCourses(3)
                        .aiCreditsPerMonth(0)
                        .maxAiGenerationsPerMonth(0)
                        .validationCreditsPerMonth(0)
                        .hourlyAiLimit(0)
                        .dailyAiLimit(0)
                        .commissionRate(0.07)
                        .features(List.of())
                        .build();
        }
    }
}
