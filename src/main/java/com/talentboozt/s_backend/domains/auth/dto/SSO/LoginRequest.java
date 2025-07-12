package com.talentboozt.s_backend.domains.auth.dto.SSO;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
