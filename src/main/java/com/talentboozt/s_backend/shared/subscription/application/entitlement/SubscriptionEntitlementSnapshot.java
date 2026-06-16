package com.talentboozt.s_backend.shared.subscription.application.entitlement;

/**
 * Effective job-portal subscription tier for authorization (ordinal aligns with {@code EntitlementPlan}).
 */
public record SubscriptionEntitlementSnapshot(int planTierOrdinal) {
}
