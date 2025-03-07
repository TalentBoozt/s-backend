package com.talentboozt.s_backend.Service.payment;

import com.talentboozt.s_backend.Model.payment.UsageDataModel;
import com.talentboozt.s_backend.Repository.payment.UsageDataRepository;
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

