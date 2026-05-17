package com.talentboozt.s_backend.domains.billing.service;

import com.talentboozt.s_backend.domains.billing.model.SubscriptionModel;
import com.talentboozt.s_backend.domains.billing.repository.mongodb.BillingSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BillingService {
    private final BillingSubscriptionRepository repository;

    public SubscriptionModel getSubscription(String orgId) {
        return repository.findByOrganizationId(orgId).orElseThrow();
    }

    public boolean canPerformAction(String orgId, String actionKey, int amount) {
        Optional<SubscriptionModel> subOpt = repository.findByOrganizationId(orgId);
        if (subOpt.isEmpty()) return false;
        
        SubscriptionModel sub = subOpt.get();
        Integer limit = sub.getUsageLimits().get(actionKey);
        Integer current = sub.getCurrentUsage().getOrDefault(actionKey, 0);
        
        return limit == null || (current + amount <= limit);
    }

    public void incrementUsage(String orgId, String actionKey, int amount) {
        SubscriptionModel sub = repository.findByOrganizationId(orgId).orElseThrow();
        sub.getCurrentUsage().put(actionKey, sub.getCurrentUsage().getOrDefault(actionKey, 0) + amount);
        repository.save(sub);
    }
}
