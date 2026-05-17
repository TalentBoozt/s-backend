package com.talentboozt.s_backend.domains.subscription.domain.model;

/**
 * Lifecycle state for a user's job-portal / monetization subscription document.
 * Wire names align with persisted LMS values for Mongo compatibility.
 */
public enum SubscriptionStatus {
    ACTIVE,
    INACTIVE,
    EXPIRED,
    CANCELLED,
    CANCEL_PENDING,
    TRIAL,
    PAST_DUE
}
