package com.talentboozt.s_backend.domains.payment.service;

import com.talentboozt.s_backend.domains.payment.model.BillingHistoryModel;
import com.talentboozt.s_backend.domains.payment.repository.mongodb.BillingHistoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class BillingHistoryService {

    @Autowired
    private BillingHistoryRepository billingHistoryRepository;

    public List<BillingHistoryModel> getBillingHistory(String companyId) {
        return billingHistoryRepository.findByCompanyId(companyId);
    }

    public BillingHistoryModel save(BillingHistoryModel billingHistory) {
        return billingHistoryRepository.save(Objects.requireNonNull(billingHistory));
    }

    public boolean existsBySessionId(String id) {
        return billingHistoryRepository.existsBySessionId(id);
    }
}

