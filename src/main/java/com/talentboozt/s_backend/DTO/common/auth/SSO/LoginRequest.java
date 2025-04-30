package com.talentboozt.s_backend.DTO.common.auth.SSO;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
