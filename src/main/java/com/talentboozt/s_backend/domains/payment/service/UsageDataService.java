package com.talentboozt.s_backend.domains.payment.service;

import com.talentboozt.s_backend.domains.payment.model.UsageDataModel;
import com.talentboozt.s_backend.domains.payment.repository.UsageDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsageDataService {

    @Autowired
    private UsageDataRepository usageDataRepository;

    public UsageDataModel getUsageData(String companyId) {
        return usageDataRepository.findByCompanyId(companyId);
    }
}

