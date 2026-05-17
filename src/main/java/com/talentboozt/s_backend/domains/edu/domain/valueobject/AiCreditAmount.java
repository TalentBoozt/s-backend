package com.talentboozt.s_backend.domains.edu.domain.valueobject;

import java.util.Objects;

/**
 * Non-negative AI credit quantity for subscription grant rules.
 */
public final class AiCreditAmount {

    private final int value;

    private AiCreditAmount(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("AI credits cannot be negative: " + value);
        }
        this.value = value;
    }

    public static AiCreditAmount of(int value) {
        return new AiCreditAmount(value);
    }

    /**
     * Normalizes plan-config values: negative misconfiguration becomes zero (legacy grant path skipped).
     */
    public static AiCreditAmount fromPlanLimitInt(int rawFromConfig) {
        return of(Math.max(0, rawFromConfig));
    }

    public static AiCreditAmount zero() {
        return new AiCreditAmount(0);
    }

    public boolean isZero() {
        return value == 0;
    }

    public boolean isPositive() {
        return value > 0;
    }

    public int asInt() {
        return value;
    }

    /**
     * Sum of this amount and {@code other}; both must be non-negative.
     */
    public AiCreditAmount plus(AiCreditAmount other) {
        Objects.requireNonNull(other, "other");
        return AiCreditAmount.of(Math.addExact(this.value, other.value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AiCreditAmount that = (AiCreditAmount) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
