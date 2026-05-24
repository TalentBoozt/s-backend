package com.talentboozt.s_backend.domains.edu.marketplace.application;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class AppPricingExperimentService {

    public Map<String, Object> assignPricingTier(String userId) {
        Map<String, Object> experimentMap = new HashMap<>();
        experimentMap.put("userId", userId);
        
        if (userId == null) {
            experimentMap.put("pricingGroup", "CONTROL");
            experimentMap.put("assignedPrice", 19.99);
            experimentMap.put("trialPeriodDays", 7);
            return experimentMap;
        }

        int hash = userId.hashCode();
        String testGroup = (hash % 2 == 0) ? "TEST_A" : "TEST_B";
        double calculatedPrice = testGroup.equals("TEST_A") ? 19.99 : 14.99;

        experimentMap.put("pricingGroup", testGroup);
        experimentMap.put("assignedPrice", calculatedPrice);
        experimentMap.put("trialPeriodDays", 7);
        
        return experimentMap;
    }
}
