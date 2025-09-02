package com.talentboozt.s_backend.domains._private.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserManagementDTO {
    private String userId;
    private String firstname;
    private String lastname;
    private String email;
    private List<String> roles;
    private boolean active;
    private boolean disabled;
    private boolean ambassador;
    private String registeredFrom;
    private List<String> accessedPlatforms;
}
