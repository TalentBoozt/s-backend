package com.talentboozt.s_backend.domains.subscription.infrastructure.mapping;

import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionStatus;
import com.talentboozt.s_backend.domains.subscription.domain.model.SubscriptionPlanCode;
import com.talentboozt.s_backend.domains.subscription.domain.model.SubscriptionStatus;

/**
 * Explicit LMS ↔ subscription enum bridges (no name-based reflection).
 */
public final class LmsPlanAndStatusMapping {

    private LmsPlanAndStatusMapping() {
    }

    public static SubscriptionPlanCode toPlanCode(ESubscriptionPlan plan) {
        if (plan == null) {
            return SubscriptionPlanCode.FREE;
        }
        return switch (plan) {
            case FREE -> SubscriptionPlanCode.FREE;
            case PRO -> SubscriptionPlanCode.PRO;
            case PREMIUM -> SubscriptionPlanCode.PREMIUM;
            case ENTERPRISE -> SubscriptionPlanCode.ENTERPRISE;
        };
    }

    public static ESubscriptionPlan toEduPlan(SubscriptionPlanCode plan) {
        if (plan == null) {
            return ESubscriptionPlan.FREE;
        }
        return switch (plan) {
            case FREE -> ESubscriptionPlan.FREE;
            case PRO -> ESubscriptionPlan.PRO;
            case PREMIUM -> ESubscriptionPlan.PREMIUM;
            case ENTERPRISE -> ESubscriptionPlan.ENTERPRISE;
        };
    }

    public static SubscriptionStatus toSubscriptionStatus(ESubscriptionStatus status) {
        if (status == null) {
            return SubscriptionStatus.ACTIVE;
        }
        return switch (status) {
            case ACTIVE -> SubscriptionStatus.ACTIVE;
            case INACTIVE -> SubscriptionStatus.INACTIVE;
            case EXPIRED -> SubscriptionStatus.EXPIRED;
            case CANCELLED -> SubscriptionStatus.CANCELLED;
            case CANCEL_PENDING -> SubscriptionStatus.CANCEL_PENDING;
            case TRIAL -> SubscriptionStatus.TRIAL;
            case PAST_DUE -> SubscriptionStatus.PAST_DUE;
        };
    }

    public static ESubscriptionStatus toEduStatus(SubscriptionStatus status) {
        if (status == null) {
            return ESubscriptionStatus.ACTIVE;
        }
        return switch (status) {
            case ACTIVE -> ESubscriptionStatus.ACTIVE;
            case INACTIVE -> ESubscriptionStatus.INACTIVE;
            case EXPIRED -> ESubscriptionStatus.EXPIRED;
            case CANCELLED -> ESubscriptionStatus.CANCELLED;
            case CANCEL_PENDING -> ESubscriptionStatus.CANCEL_PENDING;
            case TRIAL -> ESubscriptionStatus.TRIAL;
            case PAST_DUE -> ESubscriptionStatus.PAST_DUE;
        };
    }
}
