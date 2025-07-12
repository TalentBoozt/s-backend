package com.talentboozt.s_backend.domains.sys_tracking.service;

import com.talentboozt.s_backend.domains.sys_tracking.dto.GeoIPResponse;
import com.talentboozt.s_backend.domains.sys_tracking.model.TrackingEvent;
import com.talentboozt.s_backend.domains.audit_logs.service.ClientActAuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class GeoIPService {

    @Autowired
    private ClientActAuditLogService clientActAuditLogService;

    private final RestTemplate restTemplate = new RestTemplate();

    public TrackingEvent enrichWithGeoIP(TrackingEvent event) {
        try {
            String url = "http://ip-api.com/json/" + event.getIp();
            GeoIPResponse response = restTemplate.getForObject(url, GeoIPResponse.class);

            if (response != null) {
                event.setCountry(response.getCountry());
                event.setRegion(response.getRegionName());
                event.setCity(response.getCity());
            }
        } catch (Exception e) {
            clientActAuditLogService.log("geoip", event.getIp(), null, "error", "geoip", Map.of("error", e.getMessage()));
        }
        return event;
    }
}
