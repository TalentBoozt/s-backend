package com.talentboozt.s_backend.domains.lifeplanner.credits.service;

import com.talentboozt.s_backend.domains.lifeplanner.credits.model.SubscriptionTier;
import com.talentboozt.s_backend.domains.lifeplanner.credits.model.UserCredits;
import com.talentboozt.s_backend.domains.lifeplanner.credits.repository.mongodb.UserCreditsRepository;
import com.talentboozt.s_backend.domains.lifeplanner.shared.exception.InsufficientCreditsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class LifePlannerCreditService {

    private final UserCreditsRepository userCreditsRepository;

    public void deductCredits(String userId, int amount) {
        UserCredits credits = userCreditsRepository.findByUserId(userId)
                .orElseGet(() -> initializeFreeTier(userId));

        // Let's implement auto-refresh if more than a month has passed
        if (credits.getLastRefreshedAt() != null &&
            credits.getLastRefreshedAt().isBefore(Instant.now().minus(30, ChronoUnit.DAYS))) {
            credits.setCreditsAvailable(credits.getTier().getMonthlyCredits());
            credits.setLastRefreshedAt(Instant.now());
            log.info("Auto-refreshed credits for user {} to {}", userId, credits.getCreditsAvailable());
        }

        if (credits.getCreditsAvailable() < amount) {
            log.warn("User {} attempted to use {} credits but only has {}", userId, amount, credits.getCreditsAvailable());
            throw new InsufficientCreditsException("You have exhausted your AI planner credits. Please upgrade your plan.");
        }

        credits.setCreditsAvailable(credits.getCreditsAvailable() - amount);
        userCreditsRepository.save(credits);
        log.info("Deducted {} credits for user {}. Remaining: {}", amount, userId, credits.getCreditsAvailable());
    }

    public UserCredits getUserCreditsInfo(String userId) {
        return userCreditsRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserCredits newCredits = initializeFreeTier(userId);
                    return userCreditsRepository.save(newCredits);
                });
    }

    public UserCredits upgradeTier(String userId, SubscriptionTier newTier) {
        UserCredits credits = userCreditsRepository.findByUserId(userId)
                .orElse(initializeFreeTier(userId));

        credits.setTier(newTier);
        credits.setCreditsAvailable(newTier.getMonthlyCredits()); // reset immediately on upgrade
        credits.setLastRefreshedAt(Instant.now());
        
        return userCreditsRepository.save(credits);
    }

    private UserCredits initializeFreeTier(String userId) {
        UserCredits credits = new UserCredits();
        credits.setUserId(userId);
        credits.setTier(SubscriptionTier.FREE);
        credits.setCreditsAvailable(SubscriptionTier.FREE.getMonthlyCredits());
        credits.setLastRefreshedAt(Instant.now());
        return credits;
    }
}
