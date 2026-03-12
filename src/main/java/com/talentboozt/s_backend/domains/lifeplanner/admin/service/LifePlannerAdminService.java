package com.talentboozt.s_backend.domains.lifeplanner.admin.service;

import com.talentboozt.s_backend.domains.lifeplanner.credits.model.SubscriptionTier;
import com.talentboozt.s_backend.domains.lifeplanner.credits.repository.mongodb.UserCreditsRepository;
import com.talentboozt.s_backend.domains.lifeplanner.planner.repository.mongodb.StudyPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LifePlannerAdminService {

    private final UserCreditsRepository userCreditsRepository;
    private final StudyPlanRepository studyPlanRepository;

    public Map<String, Object> getPlatformStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalUsers = userCreditsRepository.count();
        long proUsers = userCreditsRepository.countByTier(SubscriptionTier.PRO);
        long premiumUsers = userCreditsRepository.countByTier(SubscriptionTier.PREMIUM);
        long activePlans = studyPlanRepository.countByStatus("ACTIVE");
        long completedPlans = studyPlanRepository.countByStatus("COMPLETED");

        double estimatedMRR = (proUsers * 9.0) + (premiumUsers * 29.0);

        stats.put("totalUsers", totalUsers);
        stats.put("proUsers", proUsers);
        stats.put("premiumUsers", premiumUsers);
        stats.put("activePlans", activePlans);
        stats.put("completedPlans", completedPlans);
        stats.put("estimatedMRR", estimatedMRR);

        return stats;
    }
}
