package com.talentboozt.s_backend.domains.edu.controller;

import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import com.talentboozt.s_backend.domains.subscription.model.Subscription;
import com.talentboozt.s_backend.domains.subscription.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/edu/subscriptions")
public class EduSubscriptionController {

    private final SubscriptionService subscriptionService;

    public EduSubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Subscription> getSubscription(@PathVariable String userId) {
        return ResponseEntity.ok(subscriptionService.getActiveSubscription(userId));
    }

    @PutMapping("/{userId}/upgrade")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE')")
    public ResponseEntity<Subscription> upgradePlan(@PathVariable String userId,
            @RequestParam ESubscriptionPlan plan) {
        return ResponseEntity.ok(subscriptionService.handleSubscriptionCreated(userId, plan, null, null));
    }

    @PutMapping("/{userId}/cancel")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('ENTERPRISE_INSTRUCTOR') or hasAuthority('SELLER_FREE')")
    public ResponseEntity<Void> cancelSubscription(@PathVariable String userId) {
        subscriptionService.downgradeToFree(userId);
        return ResponseEntity.ok().build();
    }
}
