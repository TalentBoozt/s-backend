package com.talentboozt.s_backend.DTO.common.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PasswordResetRequest {
    private String token;
    private String newPassword;
}
