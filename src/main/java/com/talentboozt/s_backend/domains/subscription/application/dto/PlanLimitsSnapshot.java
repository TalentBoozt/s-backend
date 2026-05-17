package com.talentboozt.s_backend.domains.subscription.application.dto;

import java.util.List;

/**
 * ACL snapshot of plan limits — same wire shape as the legacy {@code LimitConfig} DTO
 * (field names preserved for API compatibility).
 */
public record PlanLimitsSnapshot(
        int maxCourses,
        int aiCreditsPerMonth,
        int maxAiGenerationsPerMonth,
        int validationCreditsPerMonth,
        int hourlyAiLimit,
        int dailyAiLimit,
        double commissionRate,
        List<String> features) {

    public PlanLimitsSnapshot {
        features = features == null ? List.of() : List.copyOf(features);
    }
}
