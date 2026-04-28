package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.enums.EAffiliateStatus;
import com.talentboozt.s_backend.domains.edu.model.EAffiliateCommissions;
import com.talentboozt.s_backend.domains.edu.model.EAffiliateLinks;
import com.talentboozt.s_backend.domains.edu.model.EAffiliates;
import com.talentboozt.s_backend.domains.edu.exception.EduBadRequestException;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EAffiliateCommissionsRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EAffiliateLinksRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EAffiliatesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
public class EduAffiliateService {

    private final EAffiliatesRepository affiliatesRepository;
    private final EAffiliateLinksRepository linksRepository;
    private final EAffiliateCommissionsRepository commissionsRepository;
    private final EduLedgerService ledgerService;
    private final EduFraudDetectionService fraudDetectionService;

    public EduAffiliateService(EAffiliatesRepository affiliatesRepository,
                               EAffiliateLinksRepository linksRepository,
                               EAffiliateCommissionsRepository commissionsRepository,
                               EduLedgerService ledgerService,
                               EduFraudDetectionService fraudDetectionService) {
        this.affiliatesRepository = affiliatesRepository;
        this.linksRepository = linksRepository;
        this.commissionsRepository = commissionsRepository;
        this.ledgerService = ledgerService;
        this.fraudDetectionService = fraudDetectionService;
    }

    public EAffiliates registerAffiliate(String userId) {
        if (affiliatesRepository.findByUserId(userId).isPresent()) {
            throw new EduBadRequestException("Affiliate account already exists");
        }

        String refCode = "REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        EAffiliates affiliate = EAffiliates.builder()
                .userId(userId)
                .referralCode(refCode)
                .commissionRate(0.10) // 10% base default
                .totalEarnings(0.0)
                .status(EAffiliateStatus.ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        
        log.info("Registered new affiliate: user={}, refCode={}", userId, refCode);
        return affiliatesRepository.save(affiliate);
    }

    @Transactional
    public EAffiliateLinks generateAffiliateLink(String affiliateId, String courseId) {
        EAffiliates affiliate = affiliatesRepository.findById(affiliateId)
                .orElseThrow(() -> new EduBadRequestException("Affiliate not found"));
        
        if (affiliate.getStatus() != EAffiliateStatus.ACTIVE) {
            throw new EduBadRequestException("Affiliate account is not active");
        }

        return linksRepository.findByAffiliateIdAndCourseId(affiliateId, courseId)
                .orElseGet(() -> {
                    String trackingCode = affiliate.getReferralCode() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
                    EAffiliateLinks link = EAffiliateLinks.builder()
                            .affiliateId(affiliateId)
                            .courseId(courseId)
                            .trackingCode(trackingCode)
                            .clicks(0L)
                            .createdAt(Instant.now())
                            .build();
                    return linksRepository.save(link);
                });
    }

    public void trackAffiliateClick(String trackingCode, String ipAddress, String deviceId) {
        linksRepository.findByTrackingCode(trackingCode).ifPresent(link -> {
            fraudDetectionService.detectReferralAbuse(link.getAffiliateId(), ipAddress, deviceId);
            link.setClicks(link.getClicks() + 1);
            linksRepository.save(link);
            log.debug("Tracked affiliate click for code: {}", trackingCode);
        });
    }

    @Transactional
    public void processAffiliateCommission(String transactionId, String affiliateId, String courseId, double amount, String currency, String buyerId) {
        EAffiliates affiliate = affiliatesRepository.findById(affiliateId)
                .orElseThrow(() -> new EduBadRequestException("Affiliate not found"));

        if (affiliate.getStatus() != EAffiliateStatus.ACTIVE) {
            log.warn("Skipping commission for inactive affiliate: {}", affiliateId);
            return;
        }

        // Prevent self-affiliate abuse
        if (affiliate.getUserId().equals(buyerId)) {
            log.warn("Self-affiliate abuse detected: buyer {} is also affiliate {}", buyerId, affiliateId);
            return;
        }

        double commissionAmount = Math.round((amount * (affiliate.getCommissionRate() != null ? affiliate.getCommissionRate() : 0.10)) * 100.0) / 100.0;
        
        if (commissionAmount <= 0) return;

        // 1. Record in Commission Table
        EAffiliateCommissions commission = EAffiliateCommissions.builder()
                .affiliateId(affiliateId)
                .transactionId(transactionId)
                .courseId(courseId)
                .amount(commissionAmount)
                .currency(currency)
                .createdAt(Instant.now())
                .build();
        commissionsRepository.save(commission);

        // 2. Record in Ledger (Financial Source of Truth)
        ledgerService.recordAffiliateCommission(transactionId, affiliate.getUserId(), commissionAmount, currency, courseId);

        // 3. Update Affiliate totals
        affiliate.setTotalEarnings((affiliate.getTotalEarnings() != null ? affiliate.getTotalEarnings() : 0.0) + commissionAmount);
        affiliate.setUpdatedAt(Instant.now());
        affiliatesRepository.save(affiliate);

        log.info("Processed affiliate commission: amount={}, affiliate={}, tx={}", commissionAmount, affiliateId, transactionId);
    }
}
