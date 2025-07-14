package com.talentboozt.s_backend.domains.ambassador.service;

import com.talentboozt.s_backend.domains.ambassador.model.AmbassadorProfileModel;
import org.springframework.stereotype.Service;

@Service
public class AmbassadorLevelService {

    public String evaluateLevel(AmbassadorProfileModel profile) {
        int referrals = profile.getTotalReferrals();
        int purchases = profile.getCoursePurchasesByReferrals();
        int sessions = profile.getHostedSessions();

        if (referrals >= 100 && purchases >= 75 && sessions >= 50) {
            return "PLATINUM";
        } else if (referrals >= 75 && purchases >= 50 && sessions >= 30) {
            return "DIAMOND";
        } else if (referrals >= 50 && purchases >= 20 && sessions >= 15) {
            return "GOLD";
        } else if (referrals >= 25 && purchases >= 10 && sessions >= 5) {
            return "SILVER";
        } else {
            return "BRONZE";
        }
    }
}
