package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.enums.EHoldingStatus;
import com.talentboozt.s_backend.domains.edu.model.EHoldingLedger;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EHoldingLedgerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EduWalletScheduler {

    private final EHoldingLedgerRepository holdingLedgerRepository;
    private final EduWalletService walletService;

    /**
     * Runs daily at midnight to release funds that have passed the 14-day clearance period.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void releaseEligibleFunds() {
        log.info("Starting scheduled job: Release eligible pending funds");
        
        List<EHoldingLedger> eligibleRecords = holdingLedgerRepository.findByStatusAndClearanceDateBefore(
                EHoldingStatus.HELD, Instant.now());
        
        log.info("Found {} records eligible for clearance", eligibleRecords.size());
        
        for (EHoldingLedger record : eligibleRecords) {
            try {
                processClearance(record);
            } catch (Exception e) {
                log.error("Failed to clear ledger record {}: {}", record.getId(), e.getMessage());
            }
        }
        
        log.info("Scheduled job: Release eligible pending funds completed");
    }

    private void processClearance(EHoldingLedger record) {
        // 1. Release funds in the wallet
        walletService.releaseFunds(record.getBeneficiaryId(), record.getAmount(), record.getTransactionId());
        
        // 2. Mark ledger record as CLEARED
        record.setStatus(EHoldingStatus.CLEARED);
        record.setUpdatedAt(Instant.now());
        holdingLedgerRepository.save(record);
        
        log.info("Cleared {} funds for beneficiary {}", record.getAmount(), record.getBeneficiaryId());
    }
}
