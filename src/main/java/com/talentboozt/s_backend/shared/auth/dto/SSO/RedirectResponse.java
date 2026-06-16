package com.talentboozt.s_backend.shared.auth.dto.SSO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RedirectResponse {
    private String redirectUri;
    private String token;
    private String refreshToken;
}

