package com.talentboozt.s_backend.domains.subscription.application.entitlement;

/**
 * Effective job-portal subscription tier for authorization (ordinal aligns with {@code EntitlementPlan}).
 */
public record SubscriptionEntitlementSnapshot(int planTierOrdinal) {
}
