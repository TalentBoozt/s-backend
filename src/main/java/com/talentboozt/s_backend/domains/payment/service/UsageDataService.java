package com.talentboozt.s_backend.domains.payment.service;

import com.talentboozt.s_backend.domains.payment.model.UsageDataModel;
import com.talentboozt.s_backend.domains.payment.repository.mongodb.UsageDataRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsageDataService {

    @Autowired
    private UsageDataRepository usageDataRepository;

    @Autowired
    private SubscriptionService subscriptionService;

    public UsageDataModel getUsageData(String companyId) {
        if (subscriptionService.isExempt(companyId)) {
            UsageDataModel unlimited = new UsageDataModel();
            unlimited.setCompanyId(companyId);
            unlimited.setUsers(0);
            unlimited.setStorage(0);
            return unlimited;
        }
        return usageDataRepository.findByCompanyId(companyId);
    }
}
