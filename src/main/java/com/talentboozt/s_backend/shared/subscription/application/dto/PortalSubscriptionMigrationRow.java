package com.talentboozt.s_backend.shared.subscription.application.dto;

import com.talentboozt.s_backend.shared.subscription.domain.model.SubscriptionPlanCode;
import com.talentboozt.s_backend.shared.subscription.domain.model.SubscriptionStatus;

import java.time.Instant;

public record PortalSubscriptionMigrationRow(
        String userId,
        SubscriptionPlanCode plan,
        SubscriptionStatus status,
        Instant accountCreatedAt) {
}
