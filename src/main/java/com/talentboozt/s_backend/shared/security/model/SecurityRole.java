package com.talentboozt.s_backend.shared.security.model;

/**
 * Roles for {@link com.talentboozt.s_backend.shared.security.annotations.RequireRole}.
 * Names must match persisted LMS role enum names for resolution compatibility.
 */
public enum SecurityRole {
    LEARNER,
    SELLER_FREE,
    SELLER_PRO,
    SELLER_PREMIUM,
    PLATFORM_ADMIN,
    REVIEWER,
    ENTERPRISE_ADMIN,
    ENTERPRISE_MANAGER,
    ENTERPRISE_INSTRUCTOR,
    ENTERPRISE_LEARNER
}
