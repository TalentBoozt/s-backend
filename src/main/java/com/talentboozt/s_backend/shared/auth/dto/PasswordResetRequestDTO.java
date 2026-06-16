package com.talentboozt.s_backend.shared.auth.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PasswordResetRequestDTO {
    private String email;
}
