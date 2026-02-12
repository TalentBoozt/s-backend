package com.talentboozt.s_backend.domains.ai_tool.service;

import com.talentboozt.s_backend.domains.ai_tool.model.CreditRecord;
import com.talentboozt.s_backend.domains.ai_tool.repository.mongodb.CreditRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class CreditService {

    private final CreditRepository creditRepository;

    public CreditService(CreditRepository creditRepository) {
        this.creditRepository = creditRepository;
    }

    private static final int DAILY_CREDITS = 15;

    public boolean hasCredits(String key) {
        CreditRecord record = creditRepository.findById(key, CreditRecord.class);
        if (record == null) {
            record = new CreditRecord(key, DAILY_CREDITS, LocalDate.now());
            creditRepository.save(record);
            return true;
        }
        // Reset if a new day
        if (!record.getLastReset().isEqual(LocalDate.now())) {
            record.setCreditsRemaining(DAILY_CREDITS);
            record.setLastReset(LocalDate.now());
            creditRepository.save(record);
        }
        return record.getCreditsRemaining() > 0;
    }

    public void useCredit(String key) {
        CreditRecord record = creditRepository.findById(key, CreditRecord.class);
        if (record != null && record.getCreditsRemaining() > 0) {
            record.setCreditsRemaining(record.getCreditsRemaining() - 1);
            creditRepository.save(record);
        }
    }

    public Integer getRemainingCredits(String key) {
        CreditRecord record = creditRepository.findById(key, CreditRecord.class);
        return record != null ? record.getCreditsRemaining() : 0;
    }
}
