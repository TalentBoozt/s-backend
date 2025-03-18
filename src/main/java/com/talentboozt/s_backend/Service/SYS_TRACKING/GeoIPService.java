package com.talentboozt.s_backend.Service.SYS_TRACKING;

import com.talentboozt.s_backend.DTO.SYS_TRACKING.GeoIPResponse;
import com.talentboozt.s_backend.Model.SYS_TRACKING.TrackingEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeoIPService {
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
            // Handle API failure
            System.out.println(e.getMessage());
        }
        return event;
    }
}
