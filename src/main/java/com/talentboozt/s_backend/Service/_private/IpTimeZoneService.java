package com.talentboozt.s_backend.Service._private;

import com.talentboozt.s_backend.Shared.IpGeoData;
import com.talentboozt.s_backend.Shared.SessionContext;
import com.talentboozt.s_backend.Shared.SessionContextCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class IpTimeZoneService {

    @Autowired
    private SessionContextCache sessionContextCache;

    private final RestTemplate restTemplate = new RestTemplate();

    public IpGeoData getTimeZoneForIp(String ipAddress) {
        try {
            String url = "http://ip-api.com/json/" + ipAddress + "?fields=status,message,timezone,country,countryCode,regionName,city,isp,proxy";
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map body = response.getBody();

            if ("success".equalsIgnoreCase((String) body.get("status"))) {
                return new IpGeoData(
                        (String) body.get("timezone"),
                        (String) body.get("country"),
                        (String) body.get("countryCode"),
                        (String) body.get("regionName"),
                        (String) body.get("city"),
                        (String) body.get("isp"),
                        (boolean) body.get("proxy")
                );
            } else {
                System.out.println("IP lookup failed: " + body.get("message"));
                return null;
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch IP geo data: " + e.getMessage());
            return null;
        }
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
