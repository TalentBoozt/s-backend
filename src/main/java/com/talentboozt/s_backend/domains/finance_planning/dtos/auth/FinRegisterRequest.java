package com.talentboozt.s_backend.domains.finance_planning.dtos.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinRegisterRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
