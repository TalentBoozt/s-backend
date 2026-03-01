package com.talentboozt.s_backend.domains.auth.dto.SSO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JwtUserPayload {
    private String userId;
    private String email;
    private String userLevel;
    private List<String> roles;
    private List<String> permissions;
    private java.util.List<java.util.Map<String, String>> organizations;
}
