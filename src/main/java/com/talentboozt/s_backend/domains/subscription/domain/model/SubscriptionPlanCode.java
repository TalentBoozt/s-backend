package com.talentboozt.s_backend.domains.subscription.domain.model;

import java.util.Locale;

/**
 * Subscription tier codes owned by the subscription bounded context.
 * Wire names align with the legacy catalog ({@code ESubscriptionPlan}) for ACL mapping only.
 */
public enum SubscriptionPlanCode {
    FREE,
    PRO,
    PREMIUM,
    ENTERPRISE;

    /**
     * Parses Stripe / checkout metadata {@code plan_name} labels (explicit mapping, no reflection).
     */
    public static SubscriptionPlanCode fromStripeMetadataLabel(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("plan_name is required");
        }
        String n = raw.trim().toUpperCase(Locale.ROOT);
        return switch (n) {
            case "FREE" -> FREE;
            case "PRO" -> PRO;
            case "PREMIUM" -> PREMIUM;
            case "ENTERPRISE" -> ENTERPRISE;
            default -> throw new IllegalArgumentException("Unknown plan_name: " + raw);
        };
    }
}
