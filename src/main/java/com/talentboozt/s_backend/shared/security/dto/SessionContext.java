package com.talentboozt.s_backend.shared.security.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionContext {
    private String ip;
    private String country;
    private String timezone;
    private String countryCode;
    private String regionName;
    private String city;
    private String isp;
    private boolean proxy;
    private boolean suspectedVpn;
    private boolean suspectedBot;
    private String userAgent;

    public SessionContext(String ip, String country, String timezone, String countryCode, String regionName, String city, String isp, boolean proxy, boolean suspectedVpn, boolean suspectedBot, String userAgent) {
        this.ip = ip;
        this.country = country;
        this.timezone = timezone;
        this.countryCode = countryCode;
        this.regionName = regionName;
        this.city = city;
        this.isp = isp;
        this.proxy = proxy;
        this.suspectedVpn = suspectedVpn;
        this.suspectedBot = suspectedBot;
        this.userAgent = userAgent;
    }
}
