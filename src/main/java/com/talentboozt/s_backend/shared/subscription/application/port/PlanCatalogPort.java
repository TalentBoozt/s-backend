package com.talentboozt.s_backend.shared.subscription.application.port;

import com.talentboozt.s_backend.shared.subscription.application.dto.PlanLimitsSnapshot;
import com.talentboozt.s_backend.shared.subscription.domain.model.SubscriptionPlanCode;

/**
 * Read-only access to plan limits / entitlements catalog.
 * Implemented by an infrastructure ACL adapter backed by the legacy LMS catalog until
 * the catalog is extracted to its own module.
 */
public interface PlanCatalogPort {

    PlanLimitsSnapshot getPlanLimits(SubscriptionPlanCode plan);
}
