package com.talentboozt.s_backend.domains.edu.monetization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Subscription and Entitlement Management Service.
 * Manages user subscription tiers (FREE, PRO, CAREER_PLUS, AI_ENGINEER_TRACK)
 * in the MongoDB collection "subscription_entitlements".
 */
@Service
public class SubscriptionEntitlementService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Assigns and logs dynamic subscription plan entitlements to users.
     */
    public void saveUserEntitlement(String userId, String planType) {
        Map<String, Object> entitlementMap = new HashMap<>();
        entitlementMap.put("userId", userId);
        entitlementMap.put("planType", planType);
        entitlementMap.put("status", "ACTIVE");
        entitlementMap.put("renewedAt", new Date());

        mongoTemplate.save(entitlementMap, "subscription_entitlements");
        System.out.println("[Subscription] Assigned plan " + planType + " to user: " + userId);
    }
}
