package com.talentboozt.s_backend.domains.ai_tool.service;

import com.talentboozt.s_backend.domains.ai_tool.enums.AIUsageType;
import com.talentboozt.s_backend.domains.ai_tool.exception.InsufficientCreditsException;
import com.talentboozt.s_backend.domains.ai_tool.model.AIQuota;
import com.talentboozt.s_backend.domains.ai_tool.model.AIUsage;
import com.talentboozt.s_backend.domains.ai_tool.repository.mongodb.AIQuotaRepository;
import com.talentboozt.s_backend.domains.ai_tool.repository.mongodb.AIUsageRepository;
import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import com.talentboozt.s_backend.domains.subscription.model.Subscription;
import com.talentboozt.s_backend.domains.subscription.service.SubscriptionService;
import com.talentboozt.s_backend.domains.edu.service.PlanConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIUsageService {

    private final AIQuotaRepository quotaRepository;
    private final AIUsageRepository usageRepository;
    private final SubscriptionService subscriptionService;
    private final PlanConfigService planConfigService;

    public void checkQuota(String userId, AIUsageType type, Integer creditsRequired) {
        Subscription subscription = subscriptionService.getActiveSubscription(userId);
        ESubscriptionPlan plan = subscription != null ? subscription.getPlan() : ESubscriptionPlan.FREE;

        log.info("Checking AI quota for user: {}, plan: {}, type: {}", userId, plan, type);

        // Plan Integration Rules
        if (plan == ESubscriptionPlan.FREE) {
            throw new InsufficientCreditsException("Free plan does not include AI credits. Please upgrade.");
        }

        if (plan == ESubscriptionPlan.PRO && type == AIUsageType.VALIDATION) {
            throw new InsufficientCreditsException("PRO plan does not support AI Validation. Please upgrade to PREMIUM.");
        }

        AIQuota quota = getOrCreateQuota(userId, plan);
        if (quota.getUsed() + creditsRequired > quota.getMonthlyLimit()) {
            throw new InsufficientCreditsException("Monthly AI credit limit reached (" + quota.getMonthlyLimit() + ").");
        }
    }

    @Transactional
    public void consumeCredits(String userId, AIUsageType type, Integer credits) {
        checkQuota(userId, type, credits);
        
        AIQuota quota = getOrCreateQuota(userId, null);
        quota.setUsed(quota.getUsed() + credits);
        quotaRepository.save(quota);

        logUsage(userId, type, credits);
    }

    public void logUsage(String userId, AIUsageType type, Integer credits) {
        log.info("Logging AI usage: user={}, type={}, credits={}", userId, type, credits);
        AIUsage usage = AIUsage.builder()
                .userId(userId)
                .type(type)
                .creditsUsed(credits)
                .createdAt(Instant.now())
                .build();
        usageRepository.save(usage);
    }

    public AIQuota getOrCreateQuota(String userId, ESubscriptionPlan plan) {
        if (plan == null) {
            Subscription sub = subscriptionService.getActiveSubscription(userId);
            plan = sub != null ? sub.getPlan() : ESubscriptionPlan.FREE;
        }

        Optional<AIQuota> quotaOpt = quotaRepository.findByUserId(userId);
        if (quotaOpt.isPresent()) {
            AIQuota quota = quotaOpt.get();
            // Check if monthly reset is needed (handled by cron, but good to have safety check)
            if (Instant.now().isAfter(quota.getResetDate())) {
                resetQuota(quota, plan);
            }
            return quota;
        }

        // Initialize new quota
        AIQuota quota = AIQuota.builder()
                .userId(userId)
                .used(0)
                .resetDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .monthlyLimit(getLimitForPlan(plan))
                .build();
        return quotaRepository.save(quota);
    }

    private void resetQuota(AIQuota quota, ESubscriptionPlan plan) {
        quota.setUsed(0);
        quota.setResetDate(Instant.now().plus(30, ChronoUnit.DAYS));
        if (plan != null) {
            quota.setMonthlyLimit(getLimitForPlan(plan));
        }
    }

    private int getLimitForPlan(ESubscriptionPlan plan) {
        return planConfigService.getPlanLimits(plan).getAiCreditsPerMonth();
    }

    @Transactional
    public void resetAllMonthlyQuotas() {
        log.info("Resetting all monthly AI quotas...");
        quotaRepository.findAll().forEach(quota -> {
            quota.setUsed(0);
            quota.setResetDate(Instant.now().plus(30, ChronoUnit.DAYS));
            // In a real scenario, we might want to refresh monthlyLimit from their current plan here
            quotaRepository.save(quota);
        });
    }

    @Transactional
    public void updateQuotaForPlan(String userId, ESubscriptionPlan plan) {
        log.info("Updating AI quota for user: {} due to plan change to: {}", userId, plan);
        AIQuota quota = getOrCreateQuota(userId, plan);
        quota.setMonthlyLimit(getLimitForPlan(plan));
        // Optional: If upgrading, you might want to give them more credits immediately
        // but for now, we just update the limit.
        quotaRepository.save(quota);
    }
}
