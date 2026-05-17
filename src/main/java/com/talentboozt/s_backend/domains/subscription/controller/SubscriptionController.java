package com.talentboozt.s_backend.domains.subscription.controller;

import com.talentboozt.s_backend.shared.security.annotations.AuthenticatedUser;
import com.talentboozt.s_backend.domains.subscription.application.port.PlanCatalogPort;
import com.talentboozt.s_backend.domains.subscription.application.port.UserSubscriptionPort;
import com.talentboozt.s_backend.domains.subscription.domain.model.SubscriptionPlanCode;
import com.talentboozt.s_backend.domains.subscription.domain.model.SubscriptionStatus;
import com.talentboozt.s_backend.domains.subscription.domain.model.UserSubscription;
import com.talentboozt.s_backend.domains.subscription.dto.SubscriptionResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final UserSubscriptionPort userSubscriptionPort;
    private final PlanCatalogPort planCatalogPort;

    @GetMapping("/me")
    public ResponseEntity<SubscriptionResponseDTO> getMySubscription(@AuthenticatedUser String userId) {
        UserSubscription sub = userSubscriptionPort.getLmsSubscriptionForUser(userId);

        SubscriptionPlanCode plan = sub.plan() != null ? sub.plan() : SubscriptionPlanCode.FREE;
        SubscriptionPlanCode planForLimits = plan != null ? plan : SubscriptionPlanCode.FREE;
        var limits = planCatalogPort.getPlanLimits(planForLimits);

        SubscriptionResponseDTO response = SubscriptionResponseDTO.builder()
                .id(sub.id() != null ? sub.id() : "default-free")
                .plan(plan)
                .status(sub.status() != null ? sub.status() : SubscriptionStatus.ACTIVE)
                .expiresAt(sub.endDate())
                .features(limits.features())
                .limits(limits)
                .build();

        return ResponseEntity.ok(response);
    }
}
