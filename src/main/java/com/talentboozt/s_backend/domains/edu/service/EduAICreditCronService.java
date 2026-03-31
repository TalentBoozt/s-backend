package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.enums.ECreditLedgerActionType;
import com.talentboozt.s_backend.domains.edu.model.EAiCredits;
import com.talentboozt.s_backend.domains.edu.model.ECreditLedger;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EAiCreditsRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECreditLedgerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class EduAICreditCronService {
    
    private static final Logger logger = LoggerFactory.getLogger(EduAICreditCronService.class);
    
    private final EAiCreditsRepository creditsRepository;
    private final ECreditLedgerRepository ledgerRepository;

    public EduAICreditCronService(EAiCreditsRepository creditsRepository, ECreditLedgerRepository ledgerRepository) {
        this.creditsRepository = creditsRepository;
        this.ledgerRepository = ledgerRepository;
    }

    @Scheduled(cron = "0 0 0 * * *") // Every day at midnight
    public void expireOldCredits() {
        logger.info("Starting nightly AI Credit expiration cron job...");
        
        List<EAiCredits> expiredCredits = creditsRepository.findByExpiresAtBeforeAndBalanceGreaterThan(Instant.now(), 0);
        
        int count = 0;
        for (EAiCredits c : expiredCredits) {
            int expiredAmount = c.getBalance();
            c.setBalance(0);
            c.setUpdatedAt(Instant.now());
            creditsRepository.save(c);

            ECreditLedger ledger = ECreditLedger.builder()
                .userId(c.getUserId())
                .actionType(ECreditLedgerActionType.EXPIRE)
                .amount(-expiredAmount)
                .newBalance(0)
                .referenceId("CRON_EXPIRATION")
                .referenceType("SYSTEM_CRON")
                .metadata("Monthly credits expired from cycle limits")
                .createdAt(Instant.now())
                .build();
            ledgerRepository.save(ledger);
            count++;
        }
        
        logger.info("Cron job finished. Expired credits for {} users.", count);
    }
}
