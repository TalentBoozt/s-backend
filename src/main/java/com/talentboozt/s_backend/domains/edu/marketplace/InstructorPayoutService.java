package com.talentboozt.s_backend.domains.edu.marketplace;

import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Platform Instructor commission and payout service.
 * Tracks monthly revenue numbers, calculates commissions, and schedules net processed payouts.
 */
@Service
public class InstructorPayoutService {

    /**
     * Calculates commissions and reports processed payout balances.
     */
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
