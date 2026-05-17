package com.talentboozt.s_backend.domains.edu.domain.valueobject;

import java.util.Objects;

/**
 * Outcome of evaluating whether a monthly AI credit grant should run.
 */
public final class CreditGrantDecision {

    private final boolean shouldGrant;
    private final String reason;
    private final AiCreditAmount amount;

    private CreditGrantDecision(boolean shouldGrant, String reason, AiCreditAmount amount) {
        this.shouldGrant = shouldGrant;
        this.reason = Objects.requireNonNull(reason, "reason");
        this.amount = Objects.requireNonNull(amount, "amount");
    }

    public static CreditGrantDecision grant(AiCreditAmount amount, String reason) {
        Objects.requireNonNull(amount, "amount");
        return new CreditGrantDecision(true, reason, amount);
    }

    public static CreditGrantDecision deny(String reason, AiCreditAmount nominalAmount) {
        return new CreditGrantDecision(false, reason,
                nominalAmount != null ? nominalAmount : AiCreditAmount.zero());
    }

    public boolean shouldGrant() {
        return shouldGrant;
    }

    public String reason() {
        return reason;
    }

    /**
     * Credits to grant when {@link #shouldGrant()} is {@code true}; otherwise a nominal value (often zero).
     */
    public AiCreditAmount amount() {
        return amount;
    }
}
