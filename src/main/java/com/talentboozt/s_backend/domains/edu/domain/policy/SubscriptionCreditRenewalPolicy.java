package com.talentboozt.s_backend.domains.edu.domain.policy;

import com.talentboozt.s_backend.domains.edu.domain.valueobject.AiCreditAmount;
import com.talentboozt.s_backend.domains.edu.domain.valueobject.CreditGrantDecision;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Pure rules for LMS subscription renewal AI credit grants (no Spring / persistence).
 */
public final class SubscriptionCreditRenewalPolicy {

    public static final long CREDIT_RESET_GUARD_HOURS = 23L;

    public static final String REASON_GUARD_WINDOW = "GUARD_WINDOW";
    public static final String REASON_ZERO_MONTHLY_ALLOCATION = "ZERO_MONTHLY_ALLOCATION";
    public static final String REASON_ELIGIBLE = "ELIGIBLE";

    private SubscriptionCreditRenewalPolicy() {
    }

    /**
     * Evaluates whether a monthly credit grant should run for the configured allocation,
     * applying the last-reset guard window.
     */
    public static CreditGrantDecision evaluateMonthlyCreditGrant(
            Instant lastCreditResetAt,
            Instant now,
            AiCreditAmount configuredMonthlyAllocation) {

        if (withinCreditResetGuardWindow(lastCreditResetAt, now)) {
            return CreditGrantDecision.deny(REASON_GUARD_WINDOW, AiCreditAmount.zero());
        }
        if (configuredMonthlyAllocation.isZero()) {
            return CreditGrantDecision.deny(REASON_ZERO_MONTHLY_ALLOCATION, AiCreditAmount.zero());
        }
        return CreditGrantDecision.grant(configuredMonthlyAllocation, REASON_ELIGIBLE);
    }

    /**
     * When {@code true}, a new grant must not run (duplicate Stripe events within guard window).
     */
    private static boolean withinCreditResetGuardWindow(Instant lastCreditResetAt, Instant now) {
        if (lastCreditResetAt == null) {
            return false;
        }
        Instant guardCutoff = now.minus(CREDIT_RESET_GUARD_HOURS, ChronoUnit.HOURS);
        return lastCreditResetAt.isAfter(guardCutoff);
    }
}
