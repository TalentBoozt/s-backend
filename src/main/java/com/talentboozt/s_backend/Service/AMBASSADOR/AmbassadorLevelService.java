package com.talentboozt.s_backend.Service.AMBASSADOR;

import com.talentboozt.s_backend.Model.AMBASSADOR.AmbassadorProfileModel;
import org.springframework.stereotype.Service;

@Service
public class AmbassadorLevelService {

    public String evaluateLevel(AmbassadorProfileModel profile) {
        int referrals = profile.getTotalReferrals();
        int purchases = profile.getCoursePurchasesByReferrals();
        int sessions = profile.getHostedSessions();

        if (referrals >= 25 && purchases >= 15 && sessions >= 5) {
            return "PLATINUM";
        } else if (referrals >= 10 && purchases >= 5 && sessions >= 2) {
            return "GOLD";
        } else {
            return "BRONZE";
        }
    }
}
