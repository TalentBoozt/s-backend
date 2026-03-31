package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.enums.EHoldingStatus;
import com.talentboozt.s_backend.domains.edu.model.EHoldingLedger;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EHoldingLedgerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class EduHoldingCronService {

    private static final Logger logger = LoggerFactory.getLogger(EduHoldingCronService.class);

    private final EHoldingLedgerRepository holdingLedgerRepository;

    public EduHoldingCronService(EHoldingLedgerRepository holdingLedgerRepository) {
        this.holdingLedgerRepository = holdingLedgerRepository;
    }

    @Scheduled(cron = "0 0 1 * * *") // Every day at 1 AM
    public void clearHeldFunds() {
        logger.info("Starting daily Holding Clearance cron job...");

        List<EHoldingLedger> readyToClear = holdingLedgerRepository
                .findByStatusAndClearanceDateBefore(EHoldingStatus.HELD, Instant.now());

        int count = 0;
        for (EHoldingLedger ledger : readyToClear) {
            ledger.setStatus(EHoldingStatus.CLEARED);
            ledger.setUpdatedAt(Instant.now());
            holdingLedgerRepository.save(ledger);
            count++;
        }

        logger.info("Holding Clearance cron finished. Cleared {} pending records.", count);
    }
}
