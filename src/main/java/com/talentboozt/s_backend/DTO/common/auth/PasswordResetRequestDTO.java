package com.talentboozt.s_backend.DTO.common.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PasswordResetRequestDTO {
    private String email;
}
