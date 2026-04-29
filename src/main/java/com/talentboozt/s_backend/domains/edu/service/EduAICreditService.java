package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.dto.plan.LimitConfig;
import com.talentboozt.s_backend.domains.edu.enums.EAIUsageType;
import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import com.talentboozt.s_backend.domains.edu.model.EAiCredits;
import com.talentboozt.s_backend.domains.edu.model.EAiUsage;
import com.talentboozt.s_backend.domains.edu.exception.EduLimitExceededException;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EAiCreditsRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EAiUsageRepository;
import com.talentboozt.s_backend.domains.edu.enums.ECreditLedgerActionType;
import com.talentboozt.s_backend.domains.edu.model.ECreditLedger;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECreditLedgerRepository;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
public class EduAICreditService {

    private final EAiCreditsRepository creditsRepository;
    private final EAiUsageRepository usageRepository;
    private final ECreditLedgerRepository ledgerRepository;
    private final MongoTemplate mongoTemplate;
    private final PlanConfigService planConfigService;

    public EduAICreditService(EAiCreditsRepository creditsRepository, EAiUsageRepository usageRepository,
            ECreditLedgerRepository ledgerRepository, MongoTemplate mongoTemplate,
            PlanConfigService planConfigService) {
        this.creditsRepository = creditsRepository;
        this.usageRepository = usageRepository;
        this.ledgerRepository = ledgerRepository;
        this.mongoTemplate = mongoTemplate;
        this.planConfigService = planConfigService;
    }

    public EAiCredits getUserCredits(String userId) {
        return creditsRepository.findByUserId(userId).orElseGet(() -> {
            EAiCredits newCredits = EAiCredits.builder()
                    .userId(userId)
                    .balance(0)
                    .monthlyLimit(0)
                    .lifetimePurchased(0)
                    .lifetimeUsed(0)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .lastResetDate(Instant.now())
                    .build();
            return creditsRepository.save(newCredits);
        });
    }

    private void resetMonthlyQuotaIfNeeded(EAiCredits credits, ESubscriptionPlan plan) {
        Instant now = Instant.now();
        if (credits.getLastResetDate() == null || now.isAfter(credits.getLastResetDate().plus(30, ChronoUnit.DAYS))) {
            LimitConfig limits = planConfigService.getPlanLimits(plan);
            credits.setMonthlyLimit(limits.getAiCreditsPerMonth());
            credits.setBalance(limits.getAiCreditsPerMonth()); // Reset balance for the new month
            credits.setLastResetDate(now);
            creditsRepository.save(credits);
            log.info("Reset monthly AI quota for user: {}. New limit: {}", credits.getUserId(), limits.getAiCreditsPerMonth());
        }
    }

    /**
     * PRE-FLIGHT CHECK: Call this BEFORE making the expensive LLM call.
     * Validates rate limits (plan-aware) and sufficient credit balance
     * without deducting anything yet.
     */
    public void preValidate(String userId, int requiredCredits, ESubscriptionPlan plan) {
        EAiCredits credits = getUserCredits(userId);
        resetMonthlyQuotaIfNeeded(credits, plan);

        LimitConfig limits = planConfigService.getPlanLimits(plan);

        // Feature Gating (from AIUsageService logic)
        if (plan == ESubscriptionPlan.FREE) {
            throw new EduLimitExceededException("Free plan does not include AI credits. Please upgrade.");
        }

        // Rate limits
        int hourlyLimit = limits.getHourlyAiLimit();
        if (hourlyLimit > 0) {
            Instant last1h = Instant.now().minus(1, ChronoUnit.HOURS);
            long hourlyUsage = usageRepository.countByUserIdAndCreatedAtGreaterThanEqual(userId, last1h);
            if (hourlyUsage >= hourlyLimit) {
                throw new EduLimitExceededException(
                        "Hourly AI usage limit reached (" + hourlyLimit + " actions). Please try again later.");
            }
        }

        // Pre-check balance
        if (credits.getBalance() < requiredCredits) {
            throw new EduLimitExceededException(
                    "Insufficient AI Credits (" + credits.getBalance() + " available, " + requiredCredits
                            + " required). Please upgrade plan or buy Token packs.");
        }
    }

    /**
     * POST-GENERATION: Atomically deduct credits and record usage AFTER
     * a successful LLM call. Rate-limit checks are already done in preValidate().
     */
    public boolean deductCredits(String userId, String courseId, int amount, EAIUsageType type, String prompt,
            String response) {

        // Ensure credits document exists via get-or-create pattern first.
        getUserCredits(userId);

        // Atomic balance deduction via findAndModify
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        query.addCriteria(Criteria.where("balance").gte(amount));

        Update update = new Update();
        update.inc("balance", -amount);
        update.inc("lifetimeUsed", amount);
        update.set("updatedAt", Instant.now());

        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        EAiCredits updatedCredits = mongoTemplate.findAndModify(query, update, options, EAiCredits.class);

        if (updatedCredits == null) {
            throw new EduLimitExceededException("Insufficient AI Credits. Please upgrade plan or buy Token packs.");
        }

        // Ledger Entry
        ECreditLedger ledger = ECreditLedger.builder()
                .userId(userId)
                .actionType(ECreditLedgerActionType.DEDUCT)
                .amount(-amount)
                .balanceBefore(updatedCredits.getBalance() + amount)
                .balanceAfter(updatedCredits.getBalance())
                .newBalance(updatedCredits.getBalance())
                .referenceId(courseId)
                .referenceType(type.name())
                .metadata("Deduct via system usage.")
                .createdAt(Instant.now())
                .build();
        ledgerRepository.save(ledger);

        // Record usage
        EAiUsage usage = EAiUsage.builder()
                .userId(userId)
                .courseId(courseId)
                .prompt(prompt)
                .response(response) // Logging LLM outputs mapping for safety tuning & audit tracking
                .type(type)
                .usedCredits(amount)
                .createdBy(userId)
                .createdAt(Instant.now())
                .build();
        usageRepository.save(usage);

        return true;
    }

    // Webhook receiver for Stripe checkout
    public EAiCredits topUpCredits(String userId, int purchasedTokens, String referenceId, String referenceType) {
        EAiCredits credits = getUserCredits(userId);
        credits.setBalance(credits.getBalance() + purchasedTokens);
        credits.setLifetimePurchased(credits.getLifetimePurchased() + purchasedTokens);
        credits.setUpdatedAt(Instant.now());
        EAiCredits saved = creditsRepository.save(credits);

        ECreditLedger ledger = ECreditLedger.builder()
                .userId(userId)
                .actionType(ECreditLedgerActionType.GRANT)
                .amount(purchasedTokens)
                .balanceBefore(saved.getBalance() - purchasedTokens)
                .balanceAfter(saved.getBalance())
                .newBalance(saved.getBalance())
                .referenceId(referenceId)
                .referenceType(referenceType)
                .metadata("Top Up via System/Stripe")
                .createdAt(Instant.now())
                .build();
        ledgerRepository.save(ledger);

        return saved;
    }

    public EAiCredits grantMonthlyCredits(String userId, int tokens, int validityDays, String referenceId) {
        EAiCredits credits = getUserCredits(userId);
        credits.setBalance(credits.getBalance() + tokens);
        credits.setLifetimePurchased(credits.getLifetimePurchased() + tokens);
        credits.setExpiresAt(Instant.now().plus(validityDays, ChronoUnit.DAYS));
        credits.setUpdatedAt(Instant.now());

        EAiCredits saved = creditsRepository.save(credits);

        ECreditLedger ledger = ECreditLedger.builder()
                .userId(userId)
                .actionType(ECreditLedgerActionType.GRANT)
                .amount(tokens)
                .balanceBefore(saved.getBalance() - tokens)
                .balanceAfter(saved.getBalance())
                .newBalance(saved.getBalance())
                .referenceId(referenceId)
                .referenceType("SUBSCRIPTION_RENEWAL")
                .metadata("Monthly subscription grant")
                .createdAt(Instant.now())
                .build();
        ledgerRepository.save(ledger);

        return saved;
    }

    public List<ECreditLedger> getCreditLedger(String userId) {
        return ledgerRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public EAiCredits getQuota(String userId, ESubscriptionPlan plan) {
        EAiCredits credits = getUserCredits(userId);
        resetMonthlyQuotaIfNeeded(credits, plan);
        return credits;
    }
}
