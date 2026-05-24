package com.talentboozt.s_backend.domains.edu.marketplace.application;

import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AppInstructorPayoutService {

    public Map<String, Object> calculatePayout(String instructorId, double grossRevenueSales, double commissionRateIndex) {
        double commissionDeduction = grossRevenueSales * commissionRateIndex;
        double netPayoutBalance = grossRevenueSales - commissionDeduction;

        Map<String, Object> payoutMap = new HashMap<>();
        payoutMap.put("instructorId", instructorId);
        payoutMap.put("grossRevenue", grossRevenueSales);
        payoutMap.put("commissionDeducted", commissionDeduction);
        payoutMap.put("netPayout", netPayoutBalance);
        payoutMap.put("processedDate", new Date());
        
        return payoutMap;
    }
}
