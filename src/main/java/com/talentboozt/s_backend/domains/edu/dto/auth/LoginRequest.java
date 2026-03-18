package com.talentboozt.s_backend.domains.edu.dto.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
