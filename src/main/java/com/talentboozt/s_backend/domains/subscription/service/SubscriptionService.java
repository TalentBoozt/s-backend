package com.talentboozt.s_backend.domains.subscription.service;

import com.talentboozt.s_backend.domains.edu.enums.ERoles;
import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionStatus;
import com.talentboozt.s_backend.domains.edu.model.EUser;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EUserRepository;
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
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final EUserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final com.talentboozt.s_backend.domains.edu.service.EduAICreditService creditService;
    private final com.talentboozt.s_backend.domains.edu.service.PlanConfigService planConfigService;

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
                .filter(s -> s.getStatus() == ESubscriptionStatus.ACTIVE)
                .orElse(null);
    }

    public boolean validateUserPlan(String userId, ESubscriptionPlan requiredPlan) {
        Subscription subscription = getActiveSubscription(userId);
        if (subscription == null) {
            return requiredPlan == ESubscriptionPlan.FREE;
        }

        return subscription.getPlan().ordinal() >= requiredPlan.ordinal();
    }

    @Transactional
    public Subscription handleSubscriptionCreated(String userId, ESubscriptionPlan plan, String stripeSubscriptionId,
            Instant endDate) {
        log.info("Handling subscription creation for user: {}, plan: {}", userId, plan);

        if (!userRepository.existsById(userId)) {
            throw new SubscriptionException("User not found during subscription creation: " + userId);
        }

        Subscription subscription = subscriptionRepository.findByUserId(userId)
                .orElse(Subscription.builder().userId(userId).build());

        subscription.setPlan(plan);
        subscription.setStatus(ESubscriptionStatus.ACTIVE);
        subscription.setStripeSubscriptionId(stripeSubscriptionId);
        subscription.setStartDate(Instant.now());
        subscription.setEndDate(endDate);

        Subscription saved = subscriptionRepository.save(subscription);
        syncUserRoles(userId, plan);

        return saved;
    }

    @Transactional
    public void handleSubscriptionExpired(String stripeSubscriptionId) {
        log.info("Handling subscription expiry for stripeId: {}", stripeSubscriptionId);

        subscriptionRepository.findByStripeSubscriptionId(stripeSubscriptionId).ifPresent(sub -> {
            sub.setStatus(ESubscriptionStatus.EXPIRED);
            subscriptionRepository.save(sub);
            downgradeToFree(sub.getUserId());
        });
    }

    @Transactional
    public void downgradeToFree(String userId) {
        log.info("Downgrading user to FREE plan: {}", userId);

        Subscription subscription = subscriptionRepository.findByUserId(userId)
                .orElseThrow(() -> new SubscriptionNotFoundException(userId));

        subscription.setPlan(ESubscriptionPlan.FREE);
        subscription.setStatus(ESubscriptionStatus.ACTIVE);
        subscription.setStripeSubscriptionId(null);
        subscription.setEndDate(null);

        subscriptionRepository.save(subscription);
        syncUserRoles(userId, ESubscriptionPlan.FREE);
    }

    @Transactional
    public void handleSubscriptionDeleted(String stripeSubscriptionId) {
        log.info("Handling subscription deletion for stripeId: {}", stripeSubscriptionId);

        subscriptionRepository.findByStripeSubscriptionId(stripeSubscriptionId).ifPresent(sub -> {
            sub.setStatus(ESubscriptionStatus.CANCELLED);
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
                ESubscriptionPlan resolvedPlan = resolvePlanFromPriceId(priceId);
                if (resolvedPlan != null) {
                    sub.setPlan(resolvedPlan);
                    sub.setBillingCycle(resolveBillingCycle(priceId));
                }
            }

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
                        sub.setStatus(ESubscriptionStatus.CANCELLED);
                        downgradeToFree(sub.getUserId());
                        return;
                }
            }
            subscriptionRepository.save(sub);
            syncUserRoles(sub.getUserId(), sub.getPlan());
        });
    }

    @Transactional
    public void updatePaymentSucceeded(String stripeCustomerId) {
        subscriptionRepository.findByStripeCustomerId(stripeCustomerId).ifPresent(sub -> {
            sub.setLastPaymentAt(Instant.now());
            sub.setStatus(ESubscriptionStatus.ACTIVE);
            sub.setCancelAtPeriodEnd(false);

            if (sub.getPlan() == ESubscriptionPlan.PRO || sub.getPlan() == ESubscriptionPlan.PREMIUM
                    || sub.getPlan() == ESubscriptionPlan.ENTERPRISE) {
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

        var limits = planConfigService.getPlanLimits(sub.getPlan());
        int creditsToGrant = limits.getAiCreditsPerMonth();

        if (creditsToGrant > 0) {
            creditService.grantMonthlyCredits(sub.getUserId(), creditsToGrant, 30, referenceId);
            sub.setLastCreditResetAt(now);
        }
    }

    private ESubscriptionPlan resolvePlanFromPriceId(String priceId) {
        if (priceId == null)
            return null;
        if (priceId.equals(stripePriceProMonthly) || priceId.equals(stripePriceProYearly))
            return ESubscriptionPlan.PRO;
        if (priceId.equals(stripePricePremiumMonthly) || priceId.equals(stripePricePremiumYearly))
            return ESubscriptionPlan.PREMIUM;
        return null;
    }

    private String resolveBillingCycle(String priceId) {
        if (priceId == null)
            return null;
        if (priceId.equals(stripePriceProMonthly) || priceId.equals(stripePricePremiumMonthly))
            return "monthly";
        if (priceId.equals(stripePriceProYearly) || priceId.equals(stripePricePremiumYearly))
            return "yearly";
        return null;
    }

    private void syncUserRoles(String userId, ESubscriptionPlan newPlan) {
        userRepository.findById(userId).ifPresent(user -> {
            Set<ERoles> roles = new HashSet<>(Arrays.asList(user.getRoles() != null ? user.getRoles() : new ERoles[0]));

            // Remove existing subscription-based roles
            roles.remove(ERoles.SELLER_PRO);
            roles.remove(ERoles.SELLER_PREMIUM);
            roles.remove(ERoles.ENTERPRISE_INSTRUCTOR);
            roles.remove(ERoles.SELLER_FREE);

            // Add new role based on plan
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
                    roles.add(ERoles.SELLER_FREE);
                    break;
            }

            user.setRoles(roles.toArray(new ERoles[0]));
            user.setPlan(newPlan);
            userRepository.save(user);

            // Notify other domains about the plan change
            eventPublisher.publishEvent(new UserPlanChangedEvent(this, userId, newPlan));
        });
    }

    @Transactional
    public void updatePaymentFailed(String stripeCustomerId) {
        subscriptionRepository.findByStripeCustomerId(stripeCustomerId).ifPresent(sub -> {
            sub.setStatus(ESubscriptionStatus.PAST_DUE);
            subscriptionRepository.save(sub);
        });
    }
}
