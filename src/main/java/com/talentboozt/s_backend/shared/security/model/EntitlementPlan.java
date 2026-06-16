package com.talentboozt.s_backend.shared.security.model;

/**
 * Plan tiers for {@link com.talentboozt.s_backend.shared.security.annotations.RequirePlan}.
 * Ordinals must match {@link com.talentboozt.s_backend.shared.subscription.domain.model.SubscriptionPlanCode}.
 */
public enum EntitlementPlan {
    FREE,
    PRO,
    PREMIUM,
    ENTERPRISE
}
