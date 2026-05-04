package com.talentboozt.s_backend.domains.finance_planning.dtos.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinLoginRequest {
    private String email;
    private String password;
}
