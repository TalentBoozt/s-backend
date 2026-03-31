package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.enums.EAIUsageType;
import com.talentboozt.s_backend.domains.edu.model.EAiCredits;
import com.talentboozt.s_backend.domains.edu.model.EAiUsage;
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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class EduAICreditService {

    private final EAiCreditsRepository creditsRepository;
    private final EAiUsageRepository usageRepository;
    private final ECreditLedgerRepository ledgerRepository;
    private final MongoTemplate mongoTemplate;

    public EduAICreditService(EAiCreditsRepository creditsRepository, EAiUsageRepository usageRepository, ECreditLedgerRepository ledgerRepository, MongoTemplate mongoTemplate) {
        this.creditsRepository = creditsRepository;
        this.usageRepository = usageRepository;
        this.ledgerRepository = ledgerRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public EAiCredits getUserCredits(String userId) {
        return creditsRepository.findByUserId(userId).orElseGet(() -> {
            EAiCredits newCredits = EAiCredits.builder()
                    .userId(userId)
                    // AI credits available only for premium and enterprise users
                    .balance(0)
                    .lifetimePurchased(0)
                    .lifetimeUsed(0)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            return creditsRepository.save(newCredits);
        });
    }

    public boolean deductCredits(String userId, String courseId, int amount, EAIUsageType type, String prompt,
            String response) {
        
        // Ensure credits document exists via get-or-create pattern first.
        getUserCredits(userId);

        // Enforce daily rate limits (max 100 per 24h)
        Instant last24h = Instant.now().minus(24, ChronoUnit.HOURS);
        if (usageRepository.countByUserIdAndCreatedAtGreaterThanEqual(userId, last24h) >= 100) {
            throw new RuntimeException("Daily AI usage limit reached (100 actions). Please try again later.");
        }

        // Enforce hourly rate limits (max 20 per 1h)
        Instant last1h = Instant.now().minus(1, ChronoUnit.HOURS);
        if (usageRepository.countByUserIdAndCreatedAtGreaterThanEqual(userId, last1h) >= 20) {
            throw new RuntimeException("Hourly AI usage limit reached (20 actions). Please try again later.");
        }

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
            throw new RuntimeException("Insufficient AI Credits. Please upgrade plan or buy Token packs.");
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
}
