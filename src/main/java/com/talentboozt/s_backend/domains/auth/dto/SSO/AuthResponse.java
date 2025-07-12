package com.talentboozt.s_backend.domains.auth.dto.SSO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private JwtUserPayload user;
    private String redirectUri;
}
