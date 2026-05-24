package com.talentboozt.s_backend.domains.edu.marketplace.api;

import com.talentboozt.s_backend.domains.edu.marketplace.application.AppInstructorPayoutService;
import com.talentboozt.s_backend.domains.edu.marketplace.application.AppCreatorAnalyticsService;
import com.talentboozt.s_backend.domains.edu.marketplace.application.AppCheckoutService;
import com.talentboozt.s_backend.domains.edu.marketplace.application.AppPricingExperimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/edu/marketplace")
public class MarketplaceController {

    @Autowired
    private AppInstructorPayoutService instructorPayoutService;

    @Autowired
    private AppCreatorAnalyticsService creatorAnalyticsService;

    @Autowired
    private AppCheckoutService checkoutService;

    @Autowired
    private AppPricingExperimentService pricingExperimentService;

    @PostMapping("/payout/calculate")
    public Map<String, Object> calculatePayout(
            @RequestParam String instructorId, 
            @RequestParam double sales) {
        return instructorPayoutService.calculatePayout(instructorId, sales, 0.20);
    }

    @GetMapping("/analytics")
    public Map<String, Object> getAnalytics(@RequestParam String instructorId) {
        return creatorAnalyticsService.compileCreatorMetrics(instructorId);
    }

    @PostMapping("/checkout/stripe")
    public Map<String, Object> stripeCheckout(
            @RequestParam String userId, 
            @RequestParam String planId, 
            @RequestParam double price) {
        return checkoutService.createStripeCheckoutSession(userId, planId, price);
    }

    @PostMapping("/checkout/payhere")
    public Map<String, Object> payHereCheckout(
            @RequestParam String userId, 
            @RequestParam String planId, 
            @RequestParam double price) {
        return checkoutService.createPayHereCheckoutSession(userId, planId, price);
    }

    @GetMapping("/pricing/experiment")
    public Map<String, Object> getExperiment(@RequestParam String userId) {
        return pricingExperimentService.assignPricingTier(userId);
    }
}
