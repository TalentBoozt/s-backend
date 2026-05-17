package com.talentboozt.s_backend.domains.edu.model;

import java.time.Instant;
import java.util.Objects;

import com.talentboozt.s_backend.domains.edu.dto.plan.LimitConfig;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_subscriptions")
public class ESubscriptions {
    @Id
    private String id;
    
    @Indexed
    private String userId;
    
    @Indexed
    private ESubscriptionPlan plan;
    
    @Indexed
    private ESubscriptionStatus status;
    
    private Integer remainingCredits;
    private Integer totalCredits;
    
    private Double price;
    @Builder.Default
    private String currency = "USD";
    private String billingCycle;
    @Builder.Default
    private Boolean autoRenew = true;
    private String paymentGatewayId;
    private String stripeCustomerId;
    private String stripeSubscriptionId;
    private String stripePriceId;
    private Double commissionRate;
    private Integer maxCourses;
    private String[] features;
    
    private Instant startDate;
    private Instant endDate;
    private Instant trialEndDate;
    private Instant cancelledAt;
    private Instant lastPaymentAt;

    /** Whether Stripe will cancel at end of current period (graceful cancel). */
    @Builder.Default
    private Boolean cancelAtPeriodEnd = false;

    /** Tracks when credits were last reset to prevent duplicate grants on mid-cycle events. */
    private Instant lastCreditResetAt;
    
    @CreatedDate
    private Instant createdAt;
    
    @LastModifiedDate
    private Instant updatedAt;

    // --- lifecycle (orchestration / side effects stay in application services) ---

    /**
     * Paid renewal: records payment time and clears cancel-at-period-end while active.
     */
    public void applyRenewal(Instant paymentTime) {
        Objects.requireNonNull(paymentTime, "paymentTime");
        this.lastPaymentAt = paymentTime;
        activateSubscription();
    }

    /**
     * Subscription is active and not scheduled to cancel at period end.
     */
    public void activateSubscription() {
        this.status = ESubscriptionStatus.ACTIVE;
        this.cancelAtPeriodEnd = false;
    }

    /**
     * Stripe / billing reported a failed payment for this subscription record.
     */
    public void markPaymentFailed() {
        this.status = ESubscriptionStatus.PAST_DUE;
    }

    /**
     * User-initiated graceful cancel: retain access until period end.
     *
     * @return {@code false} when no transition applies (already FREE); {@code true} when state was updated
     */
    public boolean applyGracefulCancelPending(Instant when) {
        Objects.requireNonNull(when, "when");
        if (this.plan == ESubscriptionPlan.FREE) {
            return false;
        }
        this.status = ESubscriptionStatus.CANCEL_PENDING;
        this.cancelAtPeriodEnd = true;
        this.autoRenew = false;
        this.cancelledAt = when;
        return true;
    }

    /**
     * Local (non-Stripe) upgrade: applies plan limits, activation, and clears pending cancellation markers.
     */
    public void upgradePlan(ESubscriptionPlan newPlan, LimitConfig limits, Instant now) {
        Objects.requireNonNull(newPlan, "newPlan");
        Objects.requireNonNull(limits, "limits");
        Objects.requireNonNull(now, "now");
        applyPlanStructure(newPlan, limits);
        this.status = ESubscriptionStatus.ACTIVE;
        this.startDate = now;
        this.cancelAtPeriodEnd = false;
        this.cancelledAt = null;
    }

    /**
     * Applies structural limits from configuration (commission, courses, features).
     * ENTERPRISE may retain manual commission / maxCourses when already set.
     */
    public void applyPlanStructure(ESubscriptionPlan plan, LimitConfig limits) {
        Objects.requireNonNull(plan, "plan");
        Objects.requireNonNull(limits, "limits");
        this.plan = plan;
        if (plan != ESubscriptionPlan.ENTERPRISE || this.commissionRate == null) {
            this.commissionRate = limits.getCommissionRate();
        }
        if (plan != ESubscriptionPlan.ENTERPRISE || this.maxCourses == null) {
            this.maxCourses = limits.getMaxCourses();
        }
        this.features = limits.getFeatures().toArray(new String[0]);
        if (this.remainingCredits == null) {
            this.remainingCredits = 0;
        }
        if (this.totalCredits == null) {
            this.totalCredits = 0;
        }
    }

    public void recordCreditResetTimestamp(Instant when) {
        this.lastCreditResetAt = Objects.requireNonNull(when, "when");
    }

    /**
     * After a successful monthly credit grant: balances and last reset timestamp move together.
     */
    public void finalizeMonthlyCreditGrant(int remainingCredits, int totalCredits, Instant creditResetAt) {
        this.remainingCredits = remainingCredits;
        this.totalCredits = totalCredits;
        recordCreditResetTimestamp(creditResetAt);
    }
}
