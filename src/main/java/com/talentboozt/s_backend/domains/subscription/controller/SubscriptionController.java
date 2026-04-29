package com.talentboozt.s_backend.domains.subscription.controller;

import com.talentboozt.s_backend.domains.subscription.model.Subscription;
import com.talentboozt.s_backend.domains.subscription.service.SubscriptionService;
import com.talentboozt.s_backend.shared.security.annotations.AuthenticatedUser;
import com.talentboozt.s_backend.shared.security.model.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/me")
    public ResponseEntity<Subscription> getMySubscription(@AuthenticatedUser String userId) {
        Subscription subscription = subscriptionService.getActiveSubscription(userId);
        if (subscription == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(subscription);
    }
}
