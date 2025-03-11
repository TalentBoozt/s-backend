package com.talentboozt.s_backend.DTO.common;

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
}
