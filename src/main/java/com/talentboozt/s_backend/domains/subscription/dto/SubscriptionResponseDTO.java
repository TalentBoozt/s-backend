package com.talentboozt.s_backend.domains.subscription.dto;

import com.talentboozt.s_backend.domains.edu.dto.plan.LimitConfig;
import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionStatus;
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
    private ESubscriptionPlan plan;
    private ESubscriptionStatus status;
    private Instant expiresAt;
    private List<String> features;
    private LimitConfig limits;
}
