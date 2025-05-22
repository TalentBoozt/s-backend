package com.talentboozt.s_backend.DTO.common.auth;

import lombok.Data;

@Data
public class PermissionRequest {
    private String id;
    private String name;
    private String description;
    private String category;
}
