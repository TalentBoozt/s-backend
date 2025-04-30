package com.talentboozt.s_backend.DTO.common.auth.SSO;

import com.talentboozt.s_backend.Model.common.auth.CredentialsModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private CredentialsModel user;
    private String redirectUri;
}
