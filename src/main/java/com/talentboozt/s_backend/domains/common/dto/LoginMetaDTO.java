package com.talentboozt.s_backend.domains.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginMetaDTO {
    private String referrer;
    private String platform;
    private String promotion;
    private String provider;
    private String userAgent;
    private String language;
    private String languages;
    private String platformDetails;
    private String hardwareConcurrency;
    private String deviceMemory;
    private String cookiesEnabled;
    private String onlineStatus;
    private LocationCordDTO location;
}
