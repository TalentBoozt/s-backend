package com.talentboozt.s_backend.shared.subscription.application.entitlement;

import java.util.Set;

/**
 * Role names (same strings as persisted LMS role enum names) plus subscription tier snapshot.
 */
public record UserEntitlement(Set<String> roleNames, SubscriptionEntitlementSnapshot subscription) {
}
