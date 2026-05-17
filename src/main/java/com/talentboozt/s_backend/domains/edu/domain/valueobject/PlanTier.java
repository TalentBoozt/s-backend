package com.talentboozt.s_backend.domains.edu.domain.valueobject;

import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;

import java.util.Objects;

/**
 * Subscription plan as a tier for credit-renewal and ordering rules (backed by {@link ESubscriptionPlan}).
 */
public final class PlanTier {

    private final ESubscriptionPlan plan;

    private PlanTier(ESubscriptionPlan plan) {
        this.plan = Objects.requireNonNull(plan, "plan");
    }

    public static PlanTier from(ESubscriptionPlan plan) {
        return new PlanTier(plan);
    }

    public ESubscriptionPlan toPlan() {
        return plan;
    }

    /**
     * Ordered rank for comparison: FREE &lt; PRO &lt; PREMIUM &lt; ENTERPRISE.
     */
    public int tierOrder() {
        return switch (plan) {
            case FREE -> 0;
            case PRO -> 1;
            case PREMIUM -> 2;
            case ENTERPRISE -> 3;
        };
    }

    /**
     * {@code true} when this tier is the same or higher than {@code other}.
     */
    public boolean isAtLeast(PlanTier other) {
        Objects.requireNonNull(other, "other");
        return this.tierOrder() >= other.tierOrder();
    }

    /**
     * Stripe {@code invoice.payment_succeeded} path: tiers that receive automatic renewal credits.
     */
    public boolean receivesStripeRenewalCredits() {
        return plan == ESubscriptionPlan.PRO || plan == ESubscriptionPlan.PREMIUM || plan == ESubscriptionPlan.ENTERPRISE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PlanTier planTier = (PlanTier) o;
        return plan == planTier.plan;
    }

    @Override
    public int hashCode() {
        return Objects.hash(plan);
    }
}
