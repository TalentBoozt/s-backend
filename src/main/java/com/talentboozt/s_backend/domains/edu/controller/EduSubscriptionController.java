package com.talentboozt.s_backend.domains.edu.controller;

import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import com.talentboozt.s_backend.domains.edu.model.ESubscriptions;
import com.talentboozt.s_backend.domains.edu.service.EduSubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/edu/subscriptions")
public class EduSubscriptionController {

    private final EduSubscriptionService subscriptionService;

    public EduSubscriptionController(EduSubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ESubscriptions> getSubscription(@PathVariable String userId) {
        return ResponseEntity.ok(subscriptionService.getUserSubscription(userId));
    }

    @PutMapping("/{userId}/upgrade")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<ESubscriptions> upgradePlan(@PathVariable String userId,
            @RequestParam ESubscriptionPlan plan) {
        return ResponseEntity.ok(subscriptionService.upgradePlan(userId, plan));
    }

    @PutMapping("/{userId}/cancel")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<ESubscriptions> cancelSubscription(@PathVariable String userId) {
        return ResponseEntity.ok(subscriptionService.cancelSubscription(userId));
    }
}
