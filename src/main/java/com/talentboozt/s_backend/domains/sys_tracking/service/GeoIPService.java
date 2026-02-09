package com.talentboozt.s_backend.domains.sys_tracking.service;

import com.talentboozt.s_backend.domains.sys_tracking.dto.GeoIPResponse;
import com.talentboozt.s_backend.domains.sys_tracking.model.TrackingEvent;
import com.talentboozt.s_backend.domains.audit_logs.service.ClientActAuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;

@Service
public class GeoIPService {

    @Autowired
    private ClientActAuditLogService clientActAuditLogService;

    private final RestTemplate restTemplate;

    // Cache to prevent hitting rate limits (45 req/min for ip-api.com)
    private final Cache<String, GeoIPResponse> geoCache = Caffeine.newBuilder()
            .expireAfterWrite(12, TimeUnit.HOURS)
            .maximumSize(10_000)
            .build();

    public GeoIPService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public TrackingEvent enrichWithGeoIP(TrackingEvent event) {
        String ip = event.getIp();

        if (ip == null || ip.isEmpty() || isLocalhost(ip)) {
            return event;
        }

        // Check cache first
        GeoIPResponse cached = geoCache.getIfPresent(ip);
        if (cached != null) {
            event.setCountry(cached.getCountry());
            event.setRegion(cached.getRegionName());
            event.setCity(cached.getCity());
            return event;
        }

        try {
            String url = "http://ip-api.com/json/" + event.getIp();
            GeoIPResponse response = restTemplate.getForObject(url, GeoIPResponse.class);

            if (response != null) {
                event.setCountry(response.getCountry());
                event.setRegion(response.getRegionName());
                event.setCity(response.getCity());

                // Cache the successful response
                geoCache.put(ip, response);
            }
        } catch (Exception e) {
            clientActAuditLogService.log("geoip", event.getIp(), null, "error", "geoip", Map.of("error", e.getMessage()));
        }
        return event;
    }

    private boolean isLocalhost(String ip) {
        return ip.equals("127.0.0.1") ||
                ip.equals("0:0:0:0:0:0:0:1") ||
                ip.equals("::1") ||
                ip.startsWith("192.168.") ||
                ip.startsWith("10.") ||
                ip.startsWith("172.16.");
    }
}
