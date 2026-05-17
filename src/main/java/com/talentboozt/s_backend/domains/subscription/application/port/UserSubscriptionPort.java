package com.talentboozt.s_backend.domains.subscription.application.port;

import com.talentboozt.s_backend.domains.subscription.application.dto.PortalSubscriptionMigrationRow;
import com.talentboozt.s_backend.domains.subscription.domain.model.SubscriptionPlanCode;
import com.talentboozt.s_backend.domains.subscription.domain.model.UserSubscription;

import java.util.function.Consumer;

/**
 * Access to LMS user identity + subscription read models without coupling callers to edu types.
 */
public interface UserSubscriptionPort {

    /**
     * Delegates to legacy LMS subscription service (may persist a default FREE row).
     */
    UserSubscription getLmsSubscriptionForUser(String userId);

    /** Plan on the user profile (used for feature flags when not overridden elsewhere). */
    SubscriptionPlanCode resolvePlanCodeFromUserProfile(String userId);

    boolean userExists(String userId);

    /**
     * Updates edu_user plan + seller roles. Returns {@code true} if a user row was found and updated.
     */
    boolean applyUserPlanAndSellerRoles(String userId, SubscriptionPlanCode newPlan);

    void forEachUserForPortalSubscriptionMigration(Consumer<PortalSubscriptionMigrationRow> consumer);
}
