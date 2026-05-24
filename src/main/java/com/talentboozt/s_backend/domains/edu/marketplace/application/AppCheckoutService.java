package com.talentboozt.s_backend.domains.edu.marketplace.application;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AppCheckoutService {

    public Map<String, Object> createStripeCheckoutSession(String userId, String planId, double priceValue) {
        Map<String, Object> stripeMap = new HashMap<>();
        String testSessionId = "cs_test_" + UUID.randomUUID().toString();
        
        stripeMap.put("sessionId", testSessionId);
        stripeMap.put("userId", userId);
        stripeMap.put("planId", planId);
        stripeMap.put("amountCents", (int)(priceValue * 100));
        stripeMap.put("gateway", "STRIPE");
        stripeMap.put("checkoutUrl", "https://checkout.stripe.com/pay/" + testSessionId);
        
        return stripeMap;
    }

    public Map<String, Object> createPayHereCheckoutSession(String userId, String planId, double priceValue) {
        Map<String, Object> payHereMap = new HashMap<>();
        
        payHereMap.put("merchantId", "1210000");
        payHereMap.put("orderId", "order_" + UUID.randomUUID().toString().substring(0, 8));
        payHereMap.put("userId", userId);
        payHereMap.put("priceLkr", priceValue * 300);
        payHereMap.put("gateway", "PAYHERE");
        
        return payHereMap;
    }
}
