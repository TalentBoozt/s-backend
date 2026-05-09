package com.talentboozt.s_backend.domains.subscription.controller;

import com.talentboozt.s_backend.domains.edu.model.ESubscriptions;
import com.talentboozt.s_backend.domains.edu.service.EduSubscriptionService;
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

    private final EduSubscriptionService subscriptionService;
    private final PlanConfigService planConfigService;

    @GetMapping("/me")
    public ResponseEntity<SubscriptionResponseDTO> getMySubscription(@AuthenticatedUser String userId) {
        ESubscriptions sub = subscriptionService.getUserSubscription(userId);
        
        ESubscriptionPlan plan = sub != null ? sub.getPlan() : ESubscriptionPlan.FREE;
        var limits = planConfigService.getPlanLimits(plan);

        SubscriptionResponseDTO response = SubscriptionResponseDTO.builder()
                .id(sub != null ? sub.getId() : "default-free")
                .plan(plan)
                .status(sub != null ? sub.getStatus() : ESubscriptionStatus.ACTIVE)
                .expiresAt(sub != null ? sub.getEndDate() : null)
                .features(limits.getFeatures())
                .limits(limits)
                .build();

        return ResponseEntity.ok(response);
    }
}
