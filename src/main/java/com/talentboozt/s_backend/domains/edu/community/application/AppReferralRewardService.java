package com.talentboozt.s_backend.domains.edu.community.application;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class AppReferralRewardService {

    public Map<String, Object> rewardReferral(String referrerId, String refereeId) {
        Map<String, Object> referralMap = new HashMap<>();
        
        referralMap.put("referrerId", referrerId);
        referralMap.put("refereeId", refereeId);
        referralMap.put("xpRewardEarned", 500);
        referralMap.put("appliedCouponDiscount", "TALNOVA_FRIEND_20");
        
        return referralMap;
    }
}
