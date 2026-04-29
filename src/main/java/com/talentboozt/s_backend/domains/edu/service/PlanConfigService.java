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
                        .maxCourses(Integer.MAX_VALUE) // Unlimited courses
                        .aiCreditsPerMonth(1000) // Basic AI syllabus generator
                        .maxAiGenerationsPerMonth(100)
                        .validationCreditsPerMonth(0) // No AI validation
                        .hourlyAiLimit(10)
                        .dailyAiLimit(50)
                        .commissionRate(0.05)
                        .features(List.of("COUPONS", "BASIC_ANALYTICS", "AI_TOOLS"))
                        .build();
            case PREMIUM:
                return LimitConfig.builder()
                        .maxCourses(Integer.MAX_VALUE)
                        .aiCreditsPerMonth(5000)
                        .maxAiGenerationsPerMonth(10) // 10 content generations/month
                        .validationCreditsPerMonth(10) // 10 validations/month
                        .hourlyAiLimit(5)
                        .dailyAiLimit(10)
                        .commissionRate(0.03)
                        .features(List.of("COUPONS", "ADVANCED_ANALYTICS", "AI_TOOLS", "COURSE_VALIDATION", "PRIORITY_SUPPORT", "MARKETING_TOOLS"))
                        .build();
            case ENTERPRISE:
                return LimitConfig.builder()
                        .maxCourses(Integer.MAX_VALUE)
                        .aiCreditsPerMonth(150000)
                        .maxAiGenerationsPerMonth(25000)
                        .validationCreditsPerMonth(500)
                        .hourlyAiLimit(200)
                        .dailyAiLimit(2000)
                        .commissionRate(0.01) // 1% or custom
                        .features(List.of("COUPONS", "ADVANCED_ANALYTICS", "AI_TOOLS", "COURSE_VALIDATION", "PRIORITY_SUPPORT", "MARKETING_TOOLS", "WHITE_LABEL", "CUSTOM_DOMAIN", "API_ACCESS", "WORKSPACE"))
                        .build();
            case FREE:
            default:
                return LimitConfig.builder()
                        .maxCourses(1) // Max 1 active course
                        .aiCreditsPerMonth(0) // No AI tools
                        .maxAiGenerationsPerMonth(0)
                        .validationCreditsPerMonth(0) // No validation system
                        .hourlyAiLimit(0)
                        .dailyAiLimit(0)
                        .commissionRate(0.07)
                        .features(List.of()) // No advanced features
                        .build();
        }
    }
}
