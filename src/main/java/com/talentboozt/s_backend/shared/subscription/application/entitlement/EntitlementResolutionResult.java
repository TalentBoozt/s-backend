package com.talentboozt.s_backend.shared.subscription.application.entitlement;

/**
 * Resolved principal entitlements for HTTP RBAC (email → user id + roles + subscription tier).
 */
public record EntitlementResolutionResult(String userId, UserEntitlement userEntitlement) {
}
