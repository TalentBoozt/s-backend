package com.talentboozt.s_backend.DTO.common.auth.SSO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private JwtUserPayload user;
    private String redirectUri;
}
