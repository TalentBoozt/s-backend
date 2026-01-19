package com.talentboozt.s_backend.shared.security.service;

import com.talentboozt.s_backend.domains.audit_logs.service.ClientActAuditLogService;
import com.talentboozt.s_backend.shared.security.dto.IpGeoData;
import com.talentboozt.s_backend.shared.security.dto.SessionContext;
import com.talentboozt.s_backend.shared.security.cfg.SessionContextCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class IpTimeZoneService {

    @Autowired
    private SessionContextCache sessionContextCache;

    @Autowired
    private ClientActAuditLogService clientActAuditLogService;

    private final RestTemplate restTemplate = new RestTemplate();

    private final Cache<String, IpGeoData> ipCache = Caffeine.newBuilder()
            .expireAfterWrite(12, TimeUnit.HOURS)
            .maximumSize(100_000)
            .build();

    public IpGeoData getTimeZoneForIp(String ipAddress) {
        if (ipAddress.startsWith("192.168.") || ipAddress.startsWith("10.") || ipAddress.startsWith("127.")) {
            return null;
        }

        // ✅ Check the cache first
        IpGeoData cached = ipCache.getIfPresent(ipAddress);
        if (cached != null) {
            return cached;
        }

        try {
            String url = "http://ip-api.com/json/" + ipAddress +
                    "?fields=status,message,timezone,country,countryCode,regionName,city,isp,proxy";

            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> body = response.getBody();

            if (body != null && "success".equalsIgnoreCase((String) body.get("status"))) {
                IpGeoData geo = new IpGeoData(
                        (String) body.get("timezone"),
                        (String) body.get("country"),
                        (String) body.get("countryCode"),
                        (String) body.get("regionName"),
                        (String) body.get("city"),
                        (String) body.get("isp"),
                        (boolean) body.get("proxy")
                );

                // ✅ Cache it
                ipCache.put(ipAddress, geo);
                return geo;
            } else {
                clientActAuditLogService.log("Anonymous", ipAddress, null, "IP_LOOKUP_FAILED", "IpTimeZoneService", Map.of("message", body.get("message")));
            }
        } catch (Exception e) {
            clientActAuditLogService.log("Anonymous", ipAddress, null, "IP_LOOKUP_FAILED", "IpTimeZoneService", Map.of("error", e.getMessage()));
        }

        return null;
    }

    public void enrichSessionWithTimeZone(String sessionId, String userAgent, String ipAddress, boolean vpn) {
        // Retrieve geo data based on IP
        IpGeoData geoData = getTimeZoneForIp(ipAddress);

        if (geoData != null && geoData.getTimezone() != null) {
            String country = geoData.getCountry();
            String timezone = geoData.getTimezone();
            String countryCode = geoData.getCountryCode();
            String regionName = geoData.getRegionName();
            String city = geoData.getCity();
            String isp = geoData.getIsp();
            boolean proxy = geoData.isProxy();

            // Update session context with timezone information
            if (sessionId == null) return;
            SessionContext context = sessionContextCache.get(sessionId);
            if (context != null) {
                context.setIp(ipAddress);
                context.setCountry(country);
                context.setTimezone(timezone);
                context.setCountryCode(countryCode);
                context.setRegionName(regionName);
                context.setCity(city);
                context.setIsp(isp);
                context.setProxy(proxy);
                context.setUserAgent(userAgent);
                context.setSuspectedBot(isBot(userAgent));
                context.setSuspectedVpn(vpn);
                sessionContextCache.store(sessionId, context);
            } else {
                context = new SessionContext(ipAddress, country, timezone, countryCode, regionName, city, isp, proxy, vpn, isBot(userAgent), userAgent);
                sessionContextCache.store(sessionId, context);
            }
        }
    }

    public boolean isBot(String userAgent) {
        if (userAgent == null) return true;
        String ua = userAgent.toLowerCase();
        return ua.contains("bot") || ua.contains("crawl") || ua.contains("spider")
                || ua.contains("slurp") || ua.contains("wget") || ua.contains("curl");
    }
}
