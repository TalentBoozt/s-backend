package com.talentboozt.s_backend.domains.ai_tool.service;

import com.talentboozt.s_backend.domains.ai_tool.enums.AIUsageType;
import com.talentboozt.s_backend.domains.ai_tool.exception.InsufficientCreditsException;
import com.talentboozt.s_backend.domains.ai_tool.model.AIQuota;
import com.talentboozt.s_backend.domains.ai_tool.model.AIUsage;
import com.talentboozt.s_backend.domains.ai_tool.repository.mongodb.AIQuotaRepository;
import com.talentboozt.s_backend.domains.ai_tool.repository.mongodb.AIUsageRepository;
import com.talentboozt.s_backend.domains.subscription.application.port.PlanCatalogPort;
import com.talentboozt.s_backend.domains.subscription.domain.model.SubscriptionPlanCode;
import com.talentboozt.s_backend.domains.subscription.model.Subscription;
import com.talentboozt.s_backend.domains.subscription.service.SubscriptionService;
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
    private final PlanCatalogPort planCatalogPort;

    public void checkQuota(String userId, AIUsageType type, Integer creditsRequired) {
        Subscription subscription = subscriptionService.getActiveSubscription(userId);
        SubscriptionPlanCode plan = subscription != null && subscription.getPlan() != null
                ? subscription.getPlan()
                : SubscriptionPlanCode.FREE;

        log.info("Checking AI quota for user: {}, plan: {}, type: {}", userId, plan, type);

        if (plan == SubscriptionPlanCode.FREE) {
            throw new InsufficientCreditsException("Free plan does not include AI credits. Please upgrade.");
        }

        if (plan == SubscriptionPlanCode.PRO && type == AIUsageType.VALIDATION) {
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

    public AIQuota getOrCreateQuota(String userId, SubscriptionPlanCode plan) {
        if (plan == null) {
            Subscription sub = subscriptionService.getActiveSubscription(userId);
            plan = sub != null && sub.getPlan() != null ? sub.getPlan() : SubscriptionPlanCode.FREE;
        }

        Optional<AIQuota> quotaOpt = quotaRepository.findByUserId(userId);
        if (quotaOpt.isPresent()) {
            AIQuota quota = quotaOpt.get();
            if (Instant.now().isAfter(quota.getResetDate())) {
                resetQuota(quota, plan);
            }
            return quota;
        }

        AIQuota quota = AIQuota.builder()
                .userId(userId)
                .used(0)
                .resetDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .monthlyLimit(getLimitForPlan(plan))
                .build();
        return quotaRepository.save(quota);
    }

    private void resetQuota(AIQuota quota, SubscriptionPlanCode plan) {
        quota.setUsed(0);
        quota.setResetDate(Instant.now().plus(30, ChronoUnit.DAYS));
        if (plan != null) {
            quota.setMonthlyLimit(getLimitForPlan(plan));
        }
    }

    private int getLimitForPlan(SubscriptionPlanCode plan) {
        SubscriptionPlanCode effective = plan != null ? plan : SubscriptionPlanCode.FREE;
        return planCatalogPort.getPlanLimits(effective).aiCreditsPerMonth();
    }

    @Transactional
    public void resetAllMonthlyQuotas() {
        log.info("Resetting all monthly AI quotas...");
        quotaRepository.findAll().forEach(quota -> {
            quota.setUsed(0);
            quota.setResetDate(Instant.now().plus(30, ChronoUnit.DAYS));
            quotaRepository.save(quota);
        });
    }

    @Transactional
    public void updateQuotaForPlan(String userId, SubscriptionPlanCode plan) {
        log.info("Updating AI quota for user: {} due to plan change to: {}", userId, plan);
        AIQuota quota = getOrCreateQuota(userId, plan);
        quota.setMonthlyLimit(getLimitForPlan(plan));
        quotaRepository.save(quota);
    }
}
