package com.talentboozt.s_backend.domains.finance_planning.dtos.auth;

import com.talentboozt.s_backend.domains.finance_planning.models.FinUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinAuthResponse {
    private String accessToken;
    private String refreshToken;
    private FinUser user;
}
