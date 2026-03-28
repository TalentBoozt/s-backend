package com.talentboozt.s_backend.domains.edu.dto.auth;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class LoginRequest {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
