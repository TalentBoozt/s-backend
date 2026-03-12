package com.talentboozt.s_backend.domains.lifeplanner.credits.controller;

import com.stripe.exception.StripeException;
import com.talentboozt.s_backend.domains.lifeplanner.credits.model.SubscriptionTier;
import com.talentboozt.s_backend.domains.lifeplanner.credits.service.LPStripeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/lifeplanner/stripe")
@RequiredArgsConstructor
public class StripeCheckoutController {

    private final LPStripeService stripeService;

    @PostMapping("/checkout")
    public ResponseEntity<Map<String, String>> createCheckoutSession(
            @RequestBody Map<String, String> payload, 
            @RequestHeader("x-user-id") String userId) {
        try {
            String tierStr = payload.get("tier");
            SubscriptionTier tier = SubscriptionTier.valueOf(tierStr.toUpperCase());
            
            if (tier == SubscriptionTier.FREE) {
                return ResponseEntity.badRequest().body(Map.of("error", "Cannot checkout for free tier"));
            }
            
            String url = stripeService.createCheckoutSession(userId, tier);
            return ResponseEntity.ok(Map.of("url", url));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid tier"));
        } catch (StripeException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to create checkout session"));
        }
    }
}
