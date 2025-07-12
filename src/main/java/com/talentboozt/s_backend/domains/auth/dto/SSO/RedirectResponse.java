package com.talentboozt.s_backend.domains.auth.dto.SSO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RedirectResponse {
    private String redirectUri;
    private String token;
    private String refreshToken;
}

