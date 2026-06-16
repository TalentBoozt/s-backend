package com.talentboozt.s_backend.shared.subscription.dto;

import com.talentboozt.s_backend.shared.subscription.application.dto.PlanLimitsSnapshot;
import com.talentboozt.s_backend.shared.subscription.domain.model.SubscriptionPlanCode;
import com.talentboozt.s_backend.shared.subscription.domain.model.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponseDTO {
    private String id;
    private SubscriptionPlanCode plan;
    private SubscriptionStatus status;
    private Instant expiresAt;
    private List<String> features;
    private PlanLimitsSnapshot limits;
}
