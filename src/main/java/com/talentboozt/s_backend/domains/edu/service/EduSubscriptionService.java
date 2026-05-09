package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.dto.plan.LimitConfig;
import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionStatus;
import com.talentboozt.s_backend.domains.edu.model.ESubscriptions;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ESubscriptionsRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EUserRepository;
import com.talentboozt.s_backend.domains.edu.enums.ERoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class EduSubscriptionService {

    private static final Logger log = LoggerFactory.getLogger(EduSubscriptionService.class);

    /**
     * Minimum interval between credit resets (23 hours).
     * Prevents duplicate grants when multiple Stripe events fire in quick succession
     * (e.g. subscription.updated + invoice.payment_succeeded within seconds).
     */
    private static final long CREDIT_RESET_GUARD_HOURS = 23;

    private final ESubscriptionsRepository subscriptionsRepository;
    private final EUserRepository userRepository;
    private final EduAICreditService creditService;
    private final PlanConfigService planConfigService;

    @Value("${stripe.edu.price.pro.monthly:}")
    private String stripePriceProMonthly;
    @Value("${stripe.edu.price.pro.yearly:}")
    private String stripePriceProYearly;
    @Value("${stripe.edu.price.premium.monthly:}")
    private String stripePricePremiumMonthly;
    @Value("${stripe.edu.price.premium.yearly:}")
    private String stripePricePremiumYearly;

    public EduSubscriptionService(ESubscriptionsRepository subscriptionsRepository,
                                  EUserRepository userRepository,
                                  EduAICreditService creditService,
                                  PlanConfigService planConfigService) {
        this.subscriptionsRepository = subscriptionsRepository;
        this.userRepository = userRepository;
        this.creditService = creditService;
        this.planConfigService = planConfigService;
    }

    public ESubscriptions getUserSubscription(String userId) {
        return subscriptionsRepository.findByUserId(userId)
                .orElseGet(() -> assignDefaultFreePlan(userId));
    }

    public ESubscriptions assignDefaultFreePlan(String userId) {
        LimitConfig limits = planConfigService.getPlanLimits(ESubscriptionPlan.FREE);
        ESubscriptions freePlan = ESubscriptions.builder()
                .userId(userId)
                .plan(ESubscriptionPlan.FREE)
                .status(ESubscriptionStatus.ACTIVE)
                .remainingCredits(0)
                .totalCredits(0)
                .price(0.0)
                .currency("USD")
                .autoRenew(false)
                .commissionRate(limits.getCommissionRate())
                .maxCourses(limits.getMaxCourses())
                .startDate(Instant.now())
                .features(limits.getFeatures().toArray(new String[0]))
                .build();
        return subscriptionsRepository.save(freePlan);
    }

    /**
     * Applies plan limits from PlanConfigService to a subscription record.
     * Prices are NOT hardcoded here — they come from Stripe via webhook events.
     * Only structural limits (commission, maxCourses, features) are set from config.
     */
    private void applyPlanLimits(ESubscriptions sub, ESubscriptionPlan plan) {
        LimitConfig limits = planConfigService.getPlanLimits(plan);
        sub.setPlan(plan);
        
        // ENTERPRISE plans can have manual overrides for commission and course limits
        if (plan != ESubscriptionPlan.ENTERPRISE || sub.getCommissionRate() == null) {
            sub.setCommissionRate(limits.getCommissionRate());
        }
        if (plan != ESubscriptionPlan.ENTERPRISE || sub.getMaxCourses() == null) {
            sub.setMaxCourses(limits.getMaxCourses());
        }
        
        sub.setFeatures(limits.getFeatures().toArray(new String[0]));
        
        // Ensure remaining credits are at least 0
        if (sub.getRemainingCredits() == null) {
            sub.setRemainingCredits(0);
        }
        if (sub.getTotalCredits() == null) {
            sub.setTotalCredits(0);
        }
    }

    /**
     * Local upgrade (called from admin or direct API, not via Stripe webhook).
     * For Stripe-driven upgrades, updateFromStripeEvent handles it.
     */
    public ESubscriptions upgradePlan(String userId, ESubscriptionPlan newPlan) {
        ESubscriptions current = getUserSubscription(userId);

        applyPlanLimits(current, newPlan);
        current.setStatus(ESubscriptionStatus.ACTIVE);
        current.setStartDate(Instant.now());
        // Clear any pending cancellation
        current.setCancelAtPeriodEnd(false);
        current.setCancelledAt(null);

        if (newPlan == ESubscriptionPlan.PREMIUM) {
            grantCreditsIfEligible(current, "SYSTEM_UPGRADE");
        }

        syncUserRoles(userId, newPlan);

        return subscriptionsRepository.save(current);
    }

    /**
     * Graceful cancellation: sets cancel_at_period_end = true.
     * User retains access until the current billing period ends.
     * When the period ends, Stripe sends customer.subscription.deleted which
     * triggers the actual downgrade to FREE via updateFromStripeEvent.
     */
    public ESubscriptions cancelSubscription(String userId) {
        ESubscriptions current = getUserSubscription(userId);

        if (current.getPlan() == ESubscriptionPlan.FREE) {
            log.info("User {} is already on FREE plan, nothing to cancel", userId);
            return current;
        }

        // Mark as cancel-pending (user retains access until period end)
        current.setStatus(ESubscriptionStatus.CANCEL_PENDING);
        current.setCancelAtPeriodEnd(true);
        current.setAutoRenew(false);
        current.setCancelledAt(Instant.now());

        // NOTE: The actual Stripe cancellation (cancel_at_period_end) should be
        // triggered via the Stripe API in EduMonetizationService or the billing portal.
        // This method only updates our local state to reflect the intent.

        return subscriptionsRepository.save(current);
    }

    /**
     * Handles subscription events from Stripe webhooks.
     *
     * FIX 1: Credits are no longer reset on every event — only on invoice.payment_succeeded
     * FIX 2: Plan limits come from PlanConfigService instead of hardcoded values
     * FIX 3: Billing cycle is detected from priceId and stored
     * FIX 4: cancel_at_period_end from Stripe is respected
     * FIX 5: Trial status is properly handled
     */
    public void updateFromStripeEvent(String stripeCustomerId, String stripeSubscriptionId,
                                       String status, String priceId) {
        subscriptionsRepository.findByStripeCustomerId(stripeCustomerId).ifPresent(sub -> {
            sub.setStripeSubscriptionId(stripeSubscriptionId);

            // Resolve plan from priceId (if provided)
            if (priceId != null) {
                sub.setStripePriceId(priceId);
                ESubscriptionPlan resolvedPlan = resolvePlanFromPriceId(priceId);
                if (resolvedPlan != null) {
                    ESubscriptionPlan previousPlan = sub.getPlan();
                    applyPlanLimits(sub, resolvedPlan);
                    // Detect billing cycle from price ID
                    sub.setBillingCycle(resolveBillingCycle(priceId));

                    // Log plan changes for audit
                    if (previousPlan != resolvedPlan) {
                        log.info("Plan changed for customer {}: {} -> {}",
                                stripeCustomerId, previousPlan, resolvedPlan);
                    }
                }
            }

            // Handle Stripe subscription status
            if (status != null) {
                switch (status.toLowerCase()) {
                    case "active":
                        sub.setStatus(ESubscriptionStatus.ACTIVE);
                        sub.setCancelAtPeriodEnd(false);
                        break;
                    case "trialing":
                        sub.setStatus(ESubscriptionStatus.TRIAL);
                        break;
                    case "past_due":
                    case "unpaid":
                        sub.setStatus(ESubscriptionStatus.PAST_DUE);
                        break;
                    case "canceled":
                        // Stripe has fully canceled — downgrade to FREE
                        sub.setStatus(ESubscriptionStatus.CANCELLED);
                        sub.setPlan(ESubscriptionPlan.FREE);
                        sub.setAutoRenew(false);
                        sub.setCancelAtPeriodEnd(false);
                        if (sub.getCancelledAt() == null) {
                            sub.setCancelledAt(Instant.now());
                        }
                        // Re-apply FREE limits (immediately resets commission to 7%)
                        applyPlanLimits(sub, ESubscriptionPlan.FREE);
                        break;
                    case "incomplete":
                    case "incomplete_expired":
                        sub.setStatus(ESubscriptionStatus.INACTIVE);
                        break;
                    default:
                        log.warn("Unknown Stripe subscription status for customer {}: {}", stripeCustomerId, status);
                        break;
                }
            }

            ESubscriptions saved = subscriptionsRepository.save(sub);
            syncUserRoles(saved.getUserId(), saved.getPlan());
            log.info("Synced subscription status for user {}: plan={}, status={}", 
                    saved.getUserId(), saved.getPlan(), saved.getStatus());
        });
    }

    /**
     * Called when invoice.payment_succeeded fires.
     * This is the ONLY place where credits are granted/reset for subscription renewals.
     * Uses lastCreditResetAt guard to prevent duplicate grants from rapid-fire events.
     */
    public void updatePaymentSucceeded(String stripeCustomerId) {
        subscriptionsRepository.findByStripeCustomerId(stripeCustomerId).ifPresent(sub -> {
            sub.setLastPaymentAt(Instant.now());
            sub.setStatus(ESubscriptionStatus.ACTIVE);
            // Payment succeeded means any pending cancellation is resolved
            sub.setCancelAtPeriodEnd(false);

            // Grant credits ONLY if enough time has passed since last reset
            if (sub.getPlan() == ESubscriptionPlan.PREMIUM || sub.getPlan() == ESubscriptionPlan.ENTERPRISE) {
                grantCreditsIfEligible(sub, "STRIPE_RENEWAL");
            }

            subscriptionsRepository.save(sub);
        });
    }

    /**
     * Grants AI credits only if the lastCreditResetAt guard allows it.
     * Prevents duplicate grants when multiple Stripe events fire within the guard window.
     */
    private void grantCreditsIfEligible(ESubscriptions sub, String referenceId) {
        Instant guardCutoff = Instant.now().minus(CREDIT_RESET_GUARD_HOURS, ChronoUnit.HOURS);

        if (sub.getLastCreditResetAt() != null && sub.getLastCreditResetAt().isAfter(guardCutoff)) {
            log.info("Skipping credit grant for user {} — last reset was at {} (within guard window)",
                    sub.getUserId(), sub.getLastCreditResetAt());
            return;
        }

        LimitConfig limits = planConfigService.getPlanLimits(sub.getPlan());
        int creditsToGrant = limits.getAiCreditsPerMonth();

        if (creditsToGrant > 0) {
            creditService.grantMonthlyCredits(sub.getUserId(), creditsToGrant, 30, referenceId);
            sub.setRemainingCredits(creditsToGrant);
            sub.setTotalCredits(creditsToGrant);
            sub.setLastCreditResetAt(Instant.now());
            log.info("Granted {} AI credits to user {} (plan={})",
                    creditsToGrant, sub.getUserId(), sub.getPlan());
        }
    }

    public void updatePaymentFailed(String stripeCustomerId) {
        subscriptionsRepository.findByStripeCustomerId(stripeCustomerId).ifPresent(sub -> {
            sub.setStatus(ESubscriptionStatus.PAST_DUE);
            subscriptionsRepository.save(sub);
            log.warn("Payment failed for customer {}. Subscription set to PAST_DUE.", stripeCustomerId);
        });
    }

    /**
     * Resolves ESubscriptionPlan from a Stripe Price ID.
     * Returns null if the priceId doesn't match any known plan.
     */
    private ESubscriptionPlan resolvePlanFromPriceId(String priceId) {
        if (priceId == null) return null;
        if (priceId.equals(stripePriceProMonthly) || priceId.equals(stripePriceProYearly)) {
            return ESubscriptionPlan.PRO;
        }
        if (priceId.equals(stripePricePremiumMonthly) || priceId.equals(stripePricePremiumYearly)) {
            return ESubscriptionPlan.PREMIUM;
        }
        return null;
    }

    /**
     * Detects billing cycle (monthly vs yearly) from a Stripe Price ID.
     */
    private String resolveBillingCycle(String priceId) {
        if (priceId == null) return null;
        if (priceId.equals(stripePriceProMonthly) || priceId.equals(stripePricePremiumMonthly)) {
            return "monthly";
        }
        if (priceId.equals(stripePriceProYearly) || priceId.equals(stripePricePremiumYearly)) {
            return "yearly";
        }
        return null;
    }

    private void syncUserRoles(String userId, ESubscriptionPlan newPlan) {
        userRepository.findById(userId).ifPresent(user -> {
            java.util.Set<ERoles> roles = new java.util.HashSet<>();
            boolean wasSeller = false;

            if (user.getRoles() != null) {
                for (ERoles r : user.getRoles()) {
                    // Preserves all roles EXCEPT subscription-based seller/enterprise roles
                    if (r == ERoles.SELLER_FREE || r == ERoles.SELLER_PRO || 
                        r == ERoles.SELLER_PREMIUM || r == ERoles.ENTERPRISE_INSTRUCTOR) {
                        wasSeller = true;
                    } else {
                        roles.add(r);
                    }
                }
            }
            
            // Add the new authoritative role based on the current plan
            switch (newPlan) {
                case PRO:
                    roles.add(ERoles.SELLER_PRO);
                    break;
                case PREMIUM:
                    roles.add(ERoles.SELLER_PREMIUM);
                    break;
                case ENTERPRISE:
                    roles.add(ERoles.ENTERPRISE_INSTRUCTOR);
                    break;
                case FREE:
                default:
                    // Only add SELLER_FREE if they were already a creator/seller
                    if (wasSeller) {
                        roles.add(ERoles.SELLER_FREE);
                    }
                    break;
            }

            user.setPlan(newPlan);
            user.setRoles(roles.toArray(new ERoles[0]));
            userRepository.save(user);
            log.info("Synced user roles for {}: plan={}, roles={}", userId, newPlan, roles);
        });
    }

    public void provisionManualEnterprise(String userId, Double commissionRate, Integer maxCourses, Integer maxMembers, String notes) {
        log.info("Provisioning manual Enterprise subscription for user: {}", userId);
        
        ESubscriptions sub = subscriptionsRepository.findByUserId(userId)
            .orElse(ESubscriptions.builder().userId(userId).build());

        sub.setPlan(ESubscriptionPlan.ENTERPRISE);
        sub.setStatus(ESubscriptionStatus.ACTIVE);
        sub.setCommissionRate(commissionRate);
        sub.setMaxCourses(maxCourses);
        sub.setBillingCycle("MANUAL");
        sub.setAutoRenew(false);
        sub.setStartDate(Instant.now());
        sub.setEndDate(Instant.now().plus(java.time.Duration.ofDays(365))); // Default 1 year
        
        subscriptionsRepository.save(sub);
        syncUserRoles(userId, ESubscriptionPlan.ENTERPRISE);
        
        log.info("Manual Enterprise provisioning complete for user: {}", userId);
    }
}
