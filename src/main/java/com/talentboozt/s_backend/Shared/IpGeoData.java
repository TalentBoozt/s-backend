package com.talentboozt.s_backend.Shared;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IpGeoData {
    private String timezone;
    private String country;
    private String countryCode;
    private String regionName;
    private String city;
    private String isp;
    private boolean proxy;

    public IpGeoData(String timezone, String country, String countryCode, String regionName, String city, String isp, boolean proxy) {
        this.timezone = timezone;
        this.country = country;
        this.countryCode = countryCode;
        this.regionName = regionName;
        this.city = city;
        this.isp = isp;
        this.proxy = proxy;
    }
}
