package com.talentboozt.s_backend.domains.sys_tracking.dto.monitor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuspiciousActivityDTO {
    private String userId;
    private String role;
    private String permission;
    private String activity;
}
