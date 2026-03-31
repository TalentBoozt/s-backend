package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.model.ESubscriptions;
import org.springframework.stereotype.Service;

@Service
public class EduCommissionCalculator {

    private final EduSubscriptionService subscriptionService;

    public EduCommissionCalculator(EduSubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public double calculateCommissionRate(String sellerId) {
        ESubscriptions sub = subscriptionService.getUserSubscription(sellerId);
        if (sub == null || sub.getPlan() == null) {
            return 0.07; // Default Free
        }
        
        switch (sub.getPlan()) {
            case FREE:
                return 0.07;
            case PRO:
                return 0.05;
            case PREMIUM:
                return 0.03;
            case ENTERPRISE:
                return 0.01;
            default:
                return 0.07;
        }
    }
}
