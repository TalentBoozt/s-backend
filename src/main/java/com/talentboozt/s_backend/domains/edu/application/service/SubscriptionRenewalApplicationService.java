package com.talentboozt.s_backend.domains.edu.application.service;

import com.talentboozt.s_backend.domains.edu.domain.policy.SubscriptionCreditRenewalPolicy;
import com.talentboozt.s_backend.domains.edu.domain.valueobject.AiCreditAmount;
import com.talentboozt.s_backend.domains.edu.domain.valueobject.CreditGrantDecision;
import com.talentboozt.s_backend.domains.edu.domain.valueobject.PlanTier;
import com.talentboozt.s_backend.domains.edu.dto.plan.LimitConfig;
import com.talentboozt.s_backend.domains.edu.domain.repository.EduSubscriptionsPersistencePort;
import com.talentboozt.s_backend.domains.edu.model.ESubscriptions;
import com.talentboozt.s_backend.domains.edu.service.EduAICreditService;
import com.talentboozt.s_backend.domains.edu.service.PlanConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Orchestrates invoice payment success → subscription touch + optional AI credit renewal.
 * Persistence and external credit grant stay behind existing services/repositories.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionRenewalApplicationService {

    private final EduSubscriptionsPersistencePort subscriptionsPersistence;
    private final EduAICreditService creditService;
    private final PlanConfigService planConfigService;

    /**
     * Stripe {@code invoice.payment_succeeded} handler: marks payment, active status, optional credits.
     */
    public void onInvoicePaymentSucceeded(String stripeCustomerId) {
        subscriptionsPersistence.findByStripeCustomerId(stripeCustomerId).ifPresent(sub -> {
            sub.applyRenewal(Instant.now());

            if (PlanTier.from(sub.getPlan()).receivesStripeRenewalCredits()) {
                grantMonthlyCreditsIfEligible(sub, "STRIPE_RENEWAL");
            }

            subscriptionsPersistence.save(sub);
        });
    }

    /**
     * Grants monthly AI credits when guard allows; mutates {@code sub} in memory (caller persists).
     */
    public void grantMonthlyCreditsIfEligible(ESubscriptions sub, String referenceId) {
        Instant now = Instant.now();
        LimitConfig limits = planConfigService.getPlanLimits(sub.getPlan());
        AiCreditAmount allocation = AiCreditAmount.fromPlanLimitInt(limits.getAiCreditsPerMonth());
        CreditGrantDecision decision = SubscriptionCreditRenewalPolicy.evaluateMonthlyCreditGrant(
                sub.getLastCreditResetAt(), now, allocation);

        if (!decision.shouldGrant()) {
            if (SubscriptionCreditRenewalPolicy.REASON_GUARD_WINDOW.equals(decision.reason())) {
                log.info("Skipping credit grant for user {} — last reset was at {} (within guard window)",
                        sub.getUserId(), sub.getLastCreditResetAt());
            } else {
                log.info("Skipping credit grant for user {} — reason={}", sub.getUserId(), decision.reason());
            }
            return;
        }

        int creditsToGrant = decision.amount().asInt();
        creditService.grantMonthlyCredits(sub.getUserId(), creditsToGrant, 30, referenceId);
        sub.finalizeMonthlyCreditGrant(creditsToGrant, creditsToGrant, now);
        log.info("Granted {} AI credits to user {} (plan={})",
                creditsToGrant, sub.getUserId(), sub.getPlan());
    }
}
