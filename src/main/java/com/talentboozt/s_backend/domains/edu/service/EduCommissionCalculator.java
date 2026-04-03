package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import com.talentboozt.s_backend.domains.edu.model.EUser;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EUserRepository;
import org.springframework.stereotype.Service;

@Service
public class EduCommissionCalculator {

    private final EUserRepository userRepository;
    private final PlanConfigService planConfigService;

    public EduCommissionCalculator(EUserRepository userRepository, PlanConfigService planConfigService) {
        this.userRepository = userRepository;
        this.planConfigService = planConfigService;
    }

    public static class CommissionResult {
        public final double rate;
        public final String plan;
        public CommissionResult(double rate, String plan) {
            this.rate = rate;
            this.plan = plan;
        }
    }

    public CommissionResult calculateCommissionRate(String sellerId) {
        ESubscriptionPlan plan = ESubscriptionPlan.FREE;
        if (sellerId != null) {
            EUser user = userRepository.findById(sellerId).orElse(null);
            if (user != null && user.getPlan() != null) {
                plan = user.getPlan();
            }
        }
        return new CommissionResult(planConfigService.getPlanLimits(plan).getCommissionRate(), plan.name());
    }
}
