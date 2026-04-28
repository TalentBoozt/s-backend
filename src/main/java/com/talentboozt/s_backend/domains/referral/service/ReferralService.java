package com.talentboozt.s_backend.domains.referral.service;

import com.talentboozt.s_backend.domains.referral.enums.ReferralStatus;
import com.talentboozt.s_backend.domains.referral.enums.ReferralType;
import com.talentboozt.s_backend.domains.referral.exception.ReferralException;
import com.talentboozt.s_backend.domains.referral.model.Referral;
import com.talentboozt.s_backend.domains.referral.model.ReferralCode;
import com.talentboozt.s_backend.domains.referral.model.ReferralCommission;
import com.talentboozt.s_backend.domains.referral.repository.mongodb.ReferralCodeRepository;
import com.talentboozt.s_backend.domains.referral.repository.mongodb.ReferralCommissionRepository;
import com.talentboozt.s_backend.domains.referral.repository.mongodb.ReferralRepository;
import com.talentboozt.s_backend.domains.edu.service.EduWalletService;
import com.talentboozt.s_backend.domains.edu.service.EduAnalyticsEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class ReferralService {

    private final ReferralCodeRepository referralCodeRepository;
    private final ReferralRepository referralRepository;
    private final ReferralCommissionRepository commissionRepository;
    private final EduWalletService walletService;
    private final EduAnalyticsEventService analyticsEventService;

    public ReferralService(ReferralCodeRepository referralCodeRepository,
                         ReferralRepository referralRepository,
                         ReferralCommissionRepository commissionRepository,
                         EduWalletService walletService,
                         EduAnalyticsEventService analyticsEventService) {
        this.referralCodeRepository = referralCodeRepository;
        this.referralRepository = referralRepository;
        this.commissionRepository = commissionRepository;
        this.walletService = walletService;
        this.analyticsEventService = analyticsEventService;
    }

    public ReferralCode generateReferralCode(String userId) {
        return referralCodeRepository.findByUserId(userId)
                .orElseGet(() -> {
                    String code = generateUniqueCode();
                    ReferralCode referralCode = ReferralCode.builder()
                            .userId(userId)
                            .code(code)
                            .createdAt(Instant.now())
                            .build();
                    return referralCodeRepository.save(referralCode);
                });
    }

    public Optional<ReferralCode> validateReferralCode(String code) {
        return referralCodeRepository.findByCode(code.toUpperCase());
    }

    @Transactional
    public Referral registerReferral(String referrerId, String referredUserId, ReferralType type) {
        if (referrerId.equals(referredUserId)) {
            throw new ReferralException("Self-referral is not allowed");
        }

        if (referralRepository.existsByReferrerIdAndReferredUserId(referrerId, referredUserId)) {
            throw new ReferralException("Referral already exists between these users");
        }

        Referral referral = Referral.builder()
                .referrerId(referrerId)
                .referredUserId(referredUserId)
                .type(type)
                .status(ReferralStatus.PENDING)
                .rewardIssued(false)
                .createdAt(Instant.now())
                .build();

        Referral savedReferral = referralRepository.save(referral);
        log.info("Registered {} referral: referrer={}, referred={}", type, referrerId, referredUserId);

        if (type == ReferralType.CREATOR) {
            ReferralCommission commission = ReferralCommission.builder()
                    .referrerId(referrerId)
                    .referredCreatorId(referredUserId)
                    .percentage(5.0) // default 5% as per requirements
                    .expiryDate(Instant.now().plus(365, ChronoUnit.DAYS))
                    .createdAt(Instant.now())
                    .build();
            commissionRepository.save(commission);
            log.info("Established creator commission structure: referrer={}, creator={}", referrerId, referredUserId);
        }

        // Tracking: Referral Used
        analyticsEventService.recordEvent(com.talentboozt.s_backend.domains.edu.enums.EAnalyticsEvent.REFERRAL_USED, 
                                        referredUserId, null, java.util.Map.of("referrerId", referrerId, "type", type.name()));

        return savedReferral;
    }

    @Transactional
    public void markReferralCompleted(String referralId) {
        Referral referral = referralRepository.findById(referralId)
                .orElseThrow(() -> new ReferralException("Referral not found"));

        if (referral.getStatus() == ReferralStatus.COMPLETED) {
            return;
        }

        referral.setStatus(ReferralStatus.COMPLETED);
        referral.setCompletedAt(Instant.now());
        referralRepository.save(referral);

        processReferralReward(referralId);
    }

    @Transactional
    public void processReferralReward(String referralId) {
        Referral referral = referralRepository.findById(referralId)
                .orElseThrow(() -> new ReferralException("Referral not found"));

        if (referral.getStatus() != ReferralStatus.COMPLETED || referral.isRewardIssued()) {
            log.warn("Referral {} not eligible for reward (status: {}, rewardIssued: {})", 
                    referralId, referral.getStatus(), referral.isRewardIssued());
            return;
        }

        if (referral.getType() == ReferralType.LEARNER) {
            // Issue $5 wallet credit as default learner reward
            double rewardAmount = 5.0;
            walletService.addSale(referral.getReferrerId(), rewardAmount, "REF_REWARD_" + referral.getId());
            log.info("Issued learner referral reward of {} to referrer {}", rewardAmount, referral.getReferrerId());
        }

        referral.setRewardIssued(true);
        referralRepository.save(referral);
    }

    public List<Referral> getMyReferrals(String userId) {
        return referralRepository.findAllByReferrerId(userId);
    }

    // Integration Logic

    /**
     * Hook to be called when a purchase is finalized.
     * Completes pending referrals for first-time buyers.
     */
    public void handlePurchaseFinalization(String buyerId) {
        referralRepository.findByReferredUserId(buyerId).ifPresent(referral -> {
            if (referral.getStatus() == ReferralStatus.PENDING) {
                log.info("Triggering completion for pending referral {} after buyer {} purchase", referral.getId(), buyerId);
                markReferralCompleted(referral.getId());
            }
        });
    }

    /**
     * Calculates the commission share for the referrer if the creator was referred.
     * Source is platform commission.
     */
    public double getReferrerCommissionShare(String creatorId, double totalAmount) {
        return commissionRepository.findByReferredCreatorId(creatorId)
                .filter(c -> c.getExpiryDate() == null || c.getExpiryDate().isAfter(Instant.now()))
                .map(c -> Math.round((totalAmount * (c.getPercentage() / 100.0)) * 100.0) / 100.0)
                .orElse(0.0);
    }

    /**
     * Distributes commission to the referrer if applicable.
     */
    public void distributeCreatorReferralCommission(String creatorId, double amount, String transactionId) {
        commissionRepository.findByReferredCreatorId(creatorId)
                .filter(c -> c.getExpiryDate() == null || c.getExpiryDate().isAfter(Instant.now()))
                .ifPresent(c -> {
                    double commissionAmount = Math.round((amount * (c.getPercentage() / 100.0)) * 100.0) / 100.0;
                    if (commissionAmount > 0) {
                        walletService.addSale(c.getReferrerId(), commissionAmount, "REF_COMM_" + transactionId);
                        log.info("Distributed creator referral commission: {} to referrer {} from creator {} sale {}", 
                                commissionAmount, c.getReferrerId(), creatorId, transactionId);
                    }
                });
    }

    private String generateUniqueCode() {
        String code;
        int maxRetries = 10;
        int retries = 0;
        do {
            code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            retries++;
            if (retries > maxRetries) {
                code = UUID.randomUUID().toString().substring(0, 12).toUpperCase();
            }
        } while (referralCodeRepository.findByCode(code).isPresent());
        return code;
    }
}
