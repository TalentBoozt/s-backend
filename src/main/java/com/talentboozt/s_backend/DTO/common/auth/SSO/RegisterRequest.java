package com.talentboozt.s_backend.DTO.common.auth.SSO;

import lombok.Data;

@Data
public class RegisterRequest {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String role;
    private String userLevel;
    private String platform;
    private String referrerId;
    private String promotion;
}
