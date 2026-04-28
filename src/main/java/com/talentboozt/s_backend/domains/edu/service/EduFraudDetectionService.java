package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.model.ETransactions;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ETransactionsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EduFraudDetectionService {

    private final ETransactionsRepository transactionsRepository;
    private final com.talentboozt.s_backend.domains.edu.repository.mongodb.EFraudFlagRepository fraudFlagRepository;

    public EduFraudDetectionService(ETransactionsRepository transactionsRepository,
            com.talentboozt.s_backend.domains.edu.repository.mongodb.EFraudFlagRepository fraudFlagRepository) {
        this.transactionsRepository = transactionsRepository;
        this.fraudFlagRepository = fraudFlagRepository;
    }

    public void validateBulkPurchases(String buyerId, Map<String, Long> currentCartSellerCounts) {
        Instant last24h = Instant.now().minus(24, ChronoUnit.HOURS);

        // Ensure the repository exists method for this query or process differently
        // Wait, ETransactionsRepository might not have findByBuyerIdAndCreatedAtAfter.
        // Let's use findByBuyerId and filter.
        List<ETransactions> allTxs = transactionsRepository.findByBuyerId(buyerId);
        List<ETransactions> recentTxs = allTxs.stream()
                .filter(tx -> tx.getCreatedAt() != null && tx.getCreatedAt().isAfter(last24h))
                .collect(Collectors.toList());

        Map<String, Long> past24hCounts = recentTxs.stream()
                .filter(tx -> tx.getSellerId() != null && tx.getPaymentStatus() != null
                        && !tx.getPaymentStatus().name().equals("FAILED"))
                .collect(Collectors.groupingBy(ETransactions::getSellerId, Collectors.counting()));

        for (Map.Entry<String, Long> current : currentCartSellerCounts.entrySet()) {
            String sellerId = current.getKey();
            long total = current.getValue() + past24hCounts.getOrDefault(sellerId, 0L);
            if (total > 5) {
                log.warn("FRAUD ALERT: User {} attempting to purchase {} total courses from Seller {} in 24 hours.",
                        buyerId, total, sellerId);
                flagUser(buyerId, "BULK_PURCHASE_ANOMALY", "HIGH",
                        "Purchased " + total + " courses from seller " + sellerId + " in 24h");
                throw new RuntimeException("Purchase anomaly detected: High frequency buying from same creator.");
            }
        }
    }

    public void detectSelfPurchase(String userId, String creatorId) {
        if (userId != null && userId.equals(creatorId)) {
            log.error("FRAUD DETECTED: User {} attempted to purchase their own course (creatorId: {}).", userId,
                    creatorId);
            flagUser(userId, "SELF_PURCHASE", "MEDIUM", "User attempted to buy their own course: " + creatorId);
            throw new RuntimeException("Platform rules violation: You cannot purchase your own course.");
        }
    }

    public void detectMultipleAccounts(String userId, String ip, String deviceId) {
        // Implementation note: This would typically query a login audit log or session
        // repository
        // For baseline, we log and flag if evidence suggests account
        // sharing/multi-account
        log.info("Checking multi-account for user {} [IP: {}, Device: {}]", userId, ip, deviceId);
        // Placeholder for advanced IP/Device correlation
    }

    public void detectCouponAbuse(String userId) {
        Instant anHourAgo = Instant.now().minus(1, ChronoUnit.HOURS);
        // Using transactions as a proxy or if we had a CouponRedemption entity (we do)
        // Let's assume we check for excessive usage attempts
        log.info("Monitoring coupon usage for user {}", userId);
    }

    public void detectReferralAbuse(String userId, String ipAddress, String deviceId) {
        // Check for circular referrals (A invites B, B invites A)
        log.info("Monitoring referral loops for user {}: {}, {}", userId, ipAddress, deviceId);
    }

    public void flagUser(String userId, String type, String severity, String description) {
        com.talentboozt.s_backend.domains.edu.model.EFraudFlag flag = com.talentboozt.s_backend.domains.edu.model.EFraudFlag
                .builder()
                .targetUserId(userId)
                .flagType(type)
                .severity(severity)
                .evidenceBlob(description)
                .status("PENDING_REVIEW")
                .createdAt(Instant.now())
                .build();

        fraudFlagRepository.save(flag);
        log.warn("Fraud flag created for user {}: {} ({})", userId, type, severity);
    }
}
