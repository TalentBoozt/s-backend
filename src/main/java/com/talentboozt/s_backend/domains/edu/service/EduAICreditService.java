package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.enums.EAIUsageType;
import com.talentboozt.s_backend.domains.edu.model.EAiCredits;
import com.talentboozt.s_backend.domains.edu.model.EAiUsage;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EAiCreditsRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EAiUsageRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class EduAICreditService {

    private final EAiCreditsRepository creditsRepository;
    private final EAiUsageRepository usageRepository;

    public EduAICreditService(EAiCreditsRepository creditsRepository, EAiUsageRepository usageRepository) {
        this.creditsRepository = creditsRepository;
        this.usageRepository = usageRepository;
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
        EAiCredits credits = getUserCredits(userId);

        if (credits.getBalance() < amount) {
            throw new RuntimeException("Insufficient AI Credits. Please upgrade plan or buy Token packs.");
        }

        credits.setBalance(credits.getBalance() - amount);
        credits.setLifetimeUsed(credits.getLifetimeUsed() + amount);
        credits.setUpdatedAt(Instant.now());
        creditsRepository.save(credits);

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
    public EAiCredits topUpCredits(String userId, int purchasedTokens) {
        EAiCredits credits = getUserCredits(userId);
        credits.setBalance(credits.getBalance() + purchasedTokens);
        credits.setLifetimePurchased(credits.getLifetimePurchased() + purchasedTokens);
        credits.setUpdatedAt(Instant.now());
        return creditsRepository.save(credits);
    }
}
