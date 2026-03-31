package com.talentboozt.s_backend.domains.edu.dto.auth;

import com.talentboozt.s_backend.domains.edu.model.EUser;
import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private EUser user;
    private ESubscriptionPlan currentPlan;
}
