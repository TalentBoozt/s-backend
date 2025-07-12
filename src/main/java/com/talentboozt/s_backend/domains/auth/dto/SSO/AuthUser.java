package com.talentboozt.s_backend.domains.auth.dto.SSO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class AuthUser {
    private String token;
    private String refreshToken;
    private String employeeId;
    private String email;
    private List<Map<String, String>> organizations;
    private List<String> permissions;
    private List<String> roles;
    private String userLevel;
    private boolean active;
}
