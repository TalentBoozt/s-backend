package com.talentboozt.s_backend.domains.subscription.domain.model;

import java.time.Instant;

/**
 * Read model for a user's LMS (edu) subscription row, decoupled from persistence types.
 */
public record UserSubscription(
        String id,
        String userId,
        SubscriptionPlanCode plan,
        SubscriptionStatus status,
        Instant endDate) {
}
