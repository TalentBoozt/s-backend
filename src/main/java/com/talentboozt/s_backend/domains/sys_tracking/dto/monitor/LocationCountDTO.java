package com.talentboozt.s_backend.domains.sys_tracking.dto.monitor;

import lombok.Data;

@Data
public class LocationCountDTO {
    private String country;
    private String city;
    private Long count;
}