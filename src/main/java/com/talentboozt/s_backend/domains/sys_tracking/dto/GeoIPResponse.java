package com.talentboozt.s_backend.domains.sys_tracking.dto;

import lombok.Data;

@Data
public class GeoIPResponse {
    private String country;
    private String regionName; // Use this for region
    private String city;
}
