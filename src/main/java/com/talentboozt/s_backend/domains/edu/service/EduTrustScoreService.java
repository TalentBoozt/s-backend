package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.enums.ECourseValidationStatus;
import com.talentboozt.s_backend.domains.edu.enums.EPaymentStatus;
import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.model.EEnrollments;
import com.talentboozt.s_backend.domains.edu.model.ETransactions;
import com.talentboozt.s_backend.domains.edu.model.ETrustScores;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECoursesRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EEnrollmentsRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ETransactionsRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ETrustScoresRepository;
import com.talentboozt.s_backend.domains.edu.enums.ENotificationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EduTrustScoreService {

    private final ECoursesRepository coursesRepository;
    private final EEnrollmentsRepository enrollmentsRepository;
    private final ETransactionsRepository transactionsRepository;
    private final ETrustScoresRepository trustScoresRepository;
    private final EduNotificationService notificationService;

    public EduTrustScoreService(ECoursesRepository coursesRepository,
                                EEnrollmentsRepository enrollmentsRepository,
                                ETransactionsRepository transactionsRepository,
                                ETrustScoresRepository trustScoresRepository,
                                EduNotificationService notificationService) {
        this.coursesRepository = coursesRepository;
        this.enrollmentsRepository = enrollmentsRepository;
        this.transactionsRepository = transactionsRepository;
        this.trustScoresRepository = trustScoresRepository;
        this.notificationService = notificationService;
    }

    /**
     * Composite trust score formula (Section 9.1 of hardening plan).
     * Returns: 0-100 score broken into 6 weighted dimensions.
     */
    public ETrustScores calculateTrustScore(String creatorId) {
        List<ECourses> courses = coursesRepository.findByCreatorId(creatorId);
        if (courses.isEmpty()) {
            return getOrCreateTrustScore(creatorId);
        }

        // --- 1. Rating quality (40% weight) ---
        double avgRating = courses.stream()
                .filter(c -> c.getRating() != null && c.getRating() > 0)
                .mapToDouble(ECourses::getRating)
                .average()
                .orElse(0.0);
        int totalReviews = courses.stream().mapToInt(c -> c.getTotalReviews() != null ? c.getTotalReviews() : 0).sum();
        double ratingComp = (avgRating / 5.0) * 40.0;

        // --- 2. Completion rate (30% weight) ---
        List<String> courseIds = courses.stream().map(ECourses::getId).collect(Collectors.toList());
        List<EEnrollments> allEnrollments = courseIds.stream()
                .flatMap(cid -> enrollmentsRepository.findByCourseId(cid).stream())
                .collect(Collectors.toList());
        double avgCompletion = 0.0;
        if (!allEnrollments.isEmpty()) {
            avgCompletion = allEnrollments.stream()
                    .mapToDouble(e -> e.getProgress() != null ? e.getProgress() / 100.0 : 0.0)
                    .average()
                    .orElse(0.0);
        }
        double completionComp = avgCompletion * 30.0;

        // --- 3. Refund health (30% weight, inverted) ---
        List<ETransactions> allTxs = transactionsRepository.findBySellerId(creatorId);
        long totalTxs = allTxs.stream()
                .filter(t -> t.getPaymentStatus() == EPaymentStatus.SUCCESS 
                          || t.getPaymentStatus() == EPaymentStatus.REFUNDED)
                .count();
        long refundedTxs = allTxs.stream()
                .filter(t -> t.getPaymentStatus() == EPaymentStatus.REFUNDED)
                .count();
        double refundRateValue = totalTxs > 0 ? (double) refundedTxs / totalTxs : 0.0;
        double refundComp = (1.0 - Math.min(refundRateValue, 1.0)) * 30.0;

        // --- Bonus: Validation & Reports (keep for existing features) ---
        long verifiedCount = courses.stream()
                .filter(c -> c.getValidationStatus() == ECourseValidationStatus.AI_APPROVED
                          || Boolean.TRUE.equals(c.getTalnovaVerified()))
                .count();
        double validationBonus = (courses.isEmpty() ? 0 : (double) verifiedCount / courses.size()) * 5.0;

        // --- Composite (0-100) ---
        double totalScore = Math.min(100, Math.max(0, ratingComp + completionComp + refundComp + validationBonus));

        String tier = determineTier(totalScore);

        ETrustScores existing = getOrCreateTrustScore(creatorId);
        existing.setPreviousScore(existing.getCurrentScore());
        existing.setPreviousTier(existing.getCurrentTier());
        existing.setCurrentScore(round2(totalScore));
        existing.setCurrentTier(tier);
        
        // Store requested fields
        existing.setAverageRating(round2(avgRating));
        existing.setTotalReviews(totalReviews);
        existing.setCompletionRate(round2(avgCompletion));
        existing.setRefundRate(round2(refundRateValue));

        // Dimension scores for UI
        existing.setRatingScore(round2(ratingComp));
        existing.setCompletionScore(round2(completionComp));
        existing.setRefundHealthScore(round2(refundComp));
        existing.setValidationScore(round2(validationBonus));

        existing.setLastCalculatedAt(Instant.now());

        if (existing.getPreviousTier() != null && !tier.equals(existing.getPreviousTier())) {
            existing.setTierChangedAt(Instant.now());
        }

        // --- Tier Enforcement: Revoke featured status if tier < GOLD ---
        if (tierRank(tier) < 3) {
            courses.stream()
                    .filter(c -> Boolean.TRUE.equals(c.getIsFeatured()))
                    .forEach(c -> {
                        c.setIsFeatured(false);
                        coursesRepository.save(c);
                    });
        }

        return trustScoresRepository.save(existing);
    }

    /**
     * Requirement alias for calculateTrustScore.
     */
    public void updateScore(String creatorId) {
        calculateTrustScore(creatorId);
    }

    public ETrustScores getTrustScore(String creatorId) {
        return trustScoresRepository.findByCreatorId(creatorId)
                .orElseGet(() -> getOrCreateTrustScore(creatorId));
    }

    /**
     * Nightly cron: recalculate trust scores for all active creators.
     */
    @Scheduled(cron = "0 0 2 * * *") // 2 AM daily
    public void recalculateAllTrustScores() {
        log.info("Starting nightly Trust Score recalculation...");

        List<String> creatorIds = coursesRepository.findAll().stream()
                .map(ECourses::getCreatorId)
                .distinct()
                .collect(Collectors.toList());

        int updated = 0;
        for (String creatorId : creatorIds) {
            try {
                ETrustScores result = calculateTrustScore(creatorId);
                
                // Notify on tier downgrade
                if (result.getPreviousTier() != null 
                    && !result.getPreviousTier().equals(result.getCurrentTier())
                    && isTierDowngrade(result.getPreviousTier(), result.getCurrentTier())) {
                    notificationService.triggerNotification(
                            creatorId,
                            "Trust Score Update",
                            "Your creator tier has changed from " + result.getPreviousTier() 
                            + " to " + result.getCurrentTier() 
                            + ". Score: " + result.getCurrentScore() + "/100.",
                            ENotificationType.SYSTEM_ALERT,
                            null
                    );
                }
                updated++;
            } catch (Exception e) {
                log.error("Failed to calculate trust score for creator {}: {}", creatorId, e.getMessage());
            }
        }

        log.info("Trust Score recalculation finished. Updated {} creators.", updated);
    }

    // --- Helpers ---

    private ETrustScores getOrCreateTrustScore(String creatorId) {
        return trustScoresRepository.findByCreatorId(creatorId)
                .orElseGet(() -> {
                    ETrustScores fresh = ETrustScores.builder()
                            .creatorId(creatorId)
                            .currentScore(0.0)
                            .currentTier("BRONZE")
                            .createdAt(Instant.now())
                            .build();
                    return trustScoresRepository.save(fresh);
                });
    }

    public static String determineTier(double score) {
        if (score >= 85) return "PLATINUM";
        if (score >= 65) return "GOLD";
        if (score >= 40) return "SILVER";
        return "BRONZE";
    }

    private boolean isTierDowngrade(String oldTier, String newTier) {
        return tierRank(newTier) < tierRank(oldTier);
    }

    private int tierRank(String tier) {
        if (tier == null) return 0;
        return switch (tier) {
            case "PLATINUM" -> 4;
            case "GOLD" -> 3;
            case "SILVER" -> 2;
            case "BRONZE" -> 1;
            default -> 0;
        };
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
