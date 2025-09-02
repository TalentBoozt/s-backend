package com.talentboozt.s_backend.domains._private.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailDTO extends UserManagementDTO {
    private List<String> permissions;
    private List<Map<String, String>> organizations;
    private String userLevel;
    private String referrerId;
    private String profileStatus;
    private String profileCompleted;
    private String profileImage;
}
