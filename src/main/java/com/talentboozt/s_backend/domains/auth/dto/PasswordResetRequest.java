package com.talentboozt.s_backend.domains.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PasswordResetRequest {
    private String token;
    private String newPassword;
}
