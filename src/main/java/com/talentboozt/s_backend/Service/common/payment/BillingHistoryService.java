package com.talentboozt.s_backend.Service.common.payment;

import com.talentboozt.s_backend.Model.common.payment.BillingHistoryModel;
import com.talentboozt.s_backend.Repository.common.payment.BillingHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillingHistoryService {

    @Autowired
    private BillingHistoryRepository billingHistoryRepository;

    public List<BillingHistoryModel> getBillingHistory(String companyId) {
        return billingHistoryRepository.findByCompanyId(companyId);
    }

    public BillingHistoryModel save(BillingHistoryModel billingHistory) {
        return billingHistoryRepository.save(billingHistory);
    }
}

