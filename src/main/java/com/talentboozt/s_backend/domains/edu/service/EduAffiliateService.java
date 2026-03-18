package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.enums.EAffiliateStatus;
import com.talentboozt.s_backend.domains.edu.model.EAffiliates;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EAffiliatesRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class EduAffiliateService {

    private final EAffiliatesRepository affiliatesRepository;

    public EduAffiliateService(EAffiliatesRepository affiliatesRepository) {
        this.affiliatesRepository = affiliatesRepository;
    }

    public EAffiliates registerAffiliate(String userId) {
        if (affiliatesRepository.findByUserId(userId).isPresent()) {
            throw new RuntimeException("Affiliate account already exists");
        }

        String refCode = "REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        EAffiliates affiliate = EAffiliates.builder()
                .userId(userId)
                .referralCode(refCode)
                .commissionRate(0.10) // 10% base default commission mapping
                .totalEarnings(0.0)
                .status(EAffiliateStatus.ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        
        return affiliatesRepository.save(affiliate);
    }

    public void trackReferral(String referralCode, double transactionAmount) {
        EAffiliates mapDoc = affiliatesRepository.findByReferralCode(referralCode).orElse(null);
        if (mapDoc != null && mapDoc.getStatus() == EAffiliateStatus.ACTIVE) {
            double commission = transactionAmount * mapDoc.getCommissionRate();
            mapDoc.setTotalEarnings(mapDoc.getTotalEarnings() + commission);
            mapDoc.setUpdatedAt(Instant.now());
            affiliatesRepository.save(mapDoc);
        }
    }
}
