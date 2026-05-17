package com.talentboozt.s_backend.domains.subscription.service;

import com.talentboozt.s_backend.domains.subscription.application.port.PlanCatalogPort;
import com.talentboozt.s_backend.domains.subscription.application.port.UserSubscriptionPort;
import com.talentboozt.s_backend.domains.subscription.domain.model.SubscriptionPlanCode;
import com.talentboozt.s_backend.domains.subscription.domain.model.SubscriptionStatus;
import com.talentboozt.s_backend.domains.subscription.event.UserPlanChangedEvent;
import com.talentboozt.s_backend.domains.subscription.exception.SubscriptionException;
import com.talentboozt.s_backend.domains.subscription.exception.SubscriptionNotFoundException;
import com.talentboozt.s_backend.domains.subscription.model.Subscription;
import com.talentboozt.s_backend.domains.subscription.repository.mongodb.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserSubscriptionPort userSubscriptionPort;
    private final ApplicationEventPublisher eventPublisher;
    private final com.talentboozt.s_backend.domains.edu.service.EduAICreditService creditService;
    private final PlanCatalogPort planCatalogPort;

    @org.springframework.beans.factory.annotation.Value("${stripe.edu.price.pro.monthly:}")
    private String stripePriceProMonthly;
    @org.springframework.beans.factory.annotation.Value("${stripe.edu.price.pro.yearly:}")
    private String stripePriceProYearly;
    @org.springframework.beans.factory.annotation.Value("${stripe.edu.price.premium.monthly:}")
    private String stripePricePremiumMonthly;
    @org.springframework.beans.factory.annotation.Value("${stripe.edu.price.premium.yearly:}")
    private String stripePricePremiumYearly;

    public Subscription getActiveSubscription(String userId) {
        return subscriptionRepository.findByUserId(userId)
                .filter(s -> s.getStatus() == SubscriptionStatus.ACTIVE)
                .orElse(null);
    }

    public boolean validateUserPlan(String userId, SubscriptionPlanCode requiredPlan) {
        Subscription subscription = getActiveSubscription(userId);
        if (subscription == null) {
            return requiredPlan == SubscriptionPlanCode.FREE;
        }

        SubscriptionPlanCode current = subscription.getPlan() != null ? subscription.getPlan() : SubscriptionPlanCode.FREE;
        return current.ordinal() >= requiredPlan.ordinal();
    }

    @Transactional
    public Subscription handleSubscriptionCreated(String userId, SubscriptionPlanCode plan, String stripeSubscriptionId,
            Instant endDate) {
        log.info("Handling subscription creation for user: {}, plan: {}", userId, plan);

        if (!userSubscriptionPort.userExists(userId)) {
            throw new SubscriptionException("User not found during subscription creation: " + userId);
        }

        Subscription subscription = subscriptionRepository.findByUserId(userId)
                .orElse(Subscription.builder().userId(userId).build());

        SubscriptionPlanCode effectivePlan = plan != null ? plan : SubscriptionPlanCode.FREE;
        subscription.setPlan(effectivePlan);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStripeSubscriptionId(stripeSubscriptionId);
        subscription.setStartDate(Instant.now());
        subscription.setEndDate(endDate);

        Subscription saved = subscriptionRepository.save(subscription);
        syncUserRolesAndPublish(userId, effectivePlan);

        return saved;
    }

    @Transactional
    public void handleSubscriptionExpired(String stripeSubscriptionId) {
        log.info("Handling subscription expiry for stripeId: {}", stripeSubscriptionId);

        subscriptionRepository.findByStripeSubscriptionId(stripeSubscriptionId).ifPresent(sub -> {
            sub.setStatus(SubscriptionStatus.EXPIRED);
            subscriptionRepository.save(sub);
            downgradeToFree(sub.getUserId());
        });
    }

    @Transactional
    public void downgradeToFree(String userId) {
        log.info("Downgrading user to FREE plan: {}", userId);

        Subscription subscription = subscriptionRepository.findByUserId(userId)
                .orElseThrow(() -> new SubscriptionNotFoundException(userId));

        subscription.setPlan(SubscriptionPlanCode.FREE);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStripeSubscriptionId(null);
        subscription.setEndDate(null);

        subscriptionRepository.save(subscription);
        syncUserRolesAndPublish(userId, SubscriptionPlanCode.FREE);
    }

    @Transactional
    public void handleSubscriptionDeleted(String stripeSubscriptionId) {
        log.info("Handling subscription deletion for stripeId: {}", stripeSubscriptionId);

        subscriptionRepository.findByStripeSubscriptionId(stripeSubscriptionId).ifPresent(sub -> {
            sub.setStatus(SubscriptionStatus.CANCELLED);
            subscriptionRepository.save(sub);
            downgradeToFree(sub.getUserId());
        });
    }

    @Transactional
    public void updateFromStripeEvent(String stripeCustomerId, String stripeSubscriptionId, String status,
            String priceId) {
        log.info("Updating subscription from Stripe event: {}, status: {}", stripeSubscriptionId, status);

        subscriptionRepository.findByStripeSubscriptionId(stripeSubscriptionId).ifPresent(sub -> {
            sub.setStripeCustomerId(stripeCustomerId);
            if (priceId != null) {
                sub.setStripePriceId(priceId);
                SubscriptionPlanCode resolvedPlan = resolvePlanFromPriceId(priceId);
                if (resolvedPlan != null) {
                    sub.setPlan(resolvedPlan);
                    sub.setBillingCycle(resolveBillingCycle(priceId));
                }
            }

            if (status != null) {
                switch (status.toLowerCase()) {
                    case "active":
                        sub.setStatus(SubscriptionStatus.ACTIVE);
                        sub.setCancelAtPeriodEnd(false);
                        break;
                    case "trialing":
                        sub.setStatus(SubscriptionStatus.TRIAL);
                        break;
                    case "past_due":
                    case "unpaid":
                        sub.setStatus(SubscriptionStatus.PAST_DUE);
                        break;
                    case "canceled":
                        sub.setStatus(SubscriptionStatus.CANCELLED);
                        downgradeToFree(sub.getUserId());
                        return;
                }
            }
            subscriptionRepository.save(sub);
            SubscriptionPlanCode planForRoles = sub.getPlan() != null ? sub.getPlan() : SubscriptionPlanCode.FREE;
            syncUserRolesAndPublish(sub.getUserId(), planForRoles);
        });
    }

    @Transactional
    public void updatePaymentSucceeded(String stripeCustomerId) {
        subscriptionRepository.findByStripeCustomerId(stripeCustomerId).ifPresent(sub -> {
            sub.setLastPaymentAt(Instant.now());
            sub.setStatus(SubscriptionStatus.ACTIVE);
            sub.setCancelAtPeriodEnd(false);

            SubscriptionPlanCode p = sub.getPlan() != null ? sub.getPlan() : SubscriptionPlanCode.FREE;
            if (p == SubscriptionPlanCode.PRO || p == SubscriptionPlanCode.PREMIUM
                    || p == SubscriptionPlanCode.ENTERPRISE) {
                grantCreditsIfEligible(sub, "STRIPE_RENEWAL");
            }

            subscriptionRepository.save(sub);
        });
    }

    private void grantCreditsIfEligible(Subscription sub, String referenceId) {
        Instant now = Instant.now();
        Instant guardCutoff = now.minus(23, java.time.temporal.ChronoUnit.HOURS);

        if (sub.getLastCreditResetAt() != null && sub.getLastCreditResetAt().isAfter(guardCutoff)) {
            return;
        }

        SubscriptionPlanCode planCode = sub.getPlan() != null ? sub.getPlan() : SubscriptionPlanCode.FREE;
        var limits = planCatalogPort.getPlanLimits(planCode);
        int creditsToGrant = limits.aiCreditsPerMonth();

        if (creditsToGrant > 0) {
            creditService.grantMonthlyCredits(sub.getUserId(), creditsToGrant, 30, referenceId);
            sub.setLastCreditResetAt(now);
        }
    }

    private SubscriptionPlanCode resolvePlanFromPriceId(String priceId) {
        if (priceId == null) {
            return null;
        }
        if (priceId.equals(stripePriceProMonthly) || priceId.equals(stripePriceProYearly)) {
            return SubscriptionPlanCode.PRO;
        }
        if (priceId.equals(stripePricePremiumMonthly) || priceId.equals(stripePricePremiumYearly)) {
            return SubscriptionPlanCode.PREMIUM;
        }
        return null;
    }

    private String resolveBillingCycle(String priceId) {
        if (priceId == null) {
            return null;
        }
        if (priceId.equals(stripePriceProMonthly) || priceId.equals(stripePricePremiumMonthly)) {
            return "monthly";
        }
        if (priceId.equals(stripePriceProYearly) || priceId.equals(stripePricePremiumYearly)) {
            return "yearly";
        }
        return null;
    }

    private void syncUserRolesAndPublish(String userId, SubscriptionPlanCode newPlan) {
        SubscriptionPlanCode effective = newPlan != null ? newPlan : SubscriptionPlanCode.FREE;
        if (userSubscriptionPort.applyUserPlanAndSellerRoles(userId, effective)) {
            eventPublisher.publishEvent(new UserPlanChangedEvent(this, userId, effective));
        }
    }

    @Transactional
    public void updatePaymentFailed(String stripeCustomerId) {
        subscriptionRepository.findByStripeCustomerId(stripeCustomerId).ifPresent(sub -> {
            sub.setStatus(SubscriptionStatus.PAST_DUE);
            subscriptionRepository.save(sub);
        });
    }
}
