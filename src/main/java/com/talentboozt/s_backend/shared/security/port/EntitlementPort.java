package com.talentboozt.s_backend.shared.security.port;

import com.talentboozt.s_backend.domains.subscription.application.entitlement.EntitlementResolutionResult;

import java.util.Optional;

/**
 * Resolves subscription tier and role names for an authenticated principal (by email identifier).
 */
public interface EntitlementPort {

    Optional<EntitlementResolutionResult> resolveByEmail(String email);
}
