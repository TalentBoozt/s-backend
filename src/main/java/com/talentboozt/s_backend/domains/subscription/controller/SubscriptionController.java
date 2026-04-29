package com.talentboozt.s_backend.domains.subscription.controller;

import com.talentboozt.s_backend.domains.subscription.model.Subscription;
import com.talentboozt.s_backend.domains.subscription.service.SubscriptionService;
import com.talentboozt.s_backend.shared.security.annotations.AuthenticatedUser;
import com.talentboozt.s_backend.domains.subscription.dto.SubscriptionResponseDTO;
import com.talentboozt.s_backend.domains.edu.service.PlanConfigService;
import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionStatus;
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
    private final PlanConfigService planConfigService;

    @GetMapping("/me")
    public ResponseEntity<SubscriptionResponseDTO> getMySubscription(@AuthenticatedUser String userId) {
        Subscription subscription = subscriptionService.getActiveSubscription(userId);
        
        ESubscriptionPlan plan = subscription != null ? subscription.getPlan() : ESubscriptionPlan.FREE;
        var limits = planConfigService.getPlanLimits(plan);

        SubscriptionResponseDTO response = SubscriptionResponseDTO.builder()
                .id(subscription != null ? subscription.getId() : "default-free")
                .plan(plan)
                .status(subscription != null ? subscription.getStatus() : ESubscriptionStatus.ACTIVE)
                .expiresAt(subscription != null ? subscription.getEndDate() : null)
                .features(limits.getFeatures())
                .limits(limits)
                .build();

        return ResponseEntity.ok(response);
    }
}
