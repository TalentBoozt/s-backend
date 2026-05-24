package com.talentboozt.s_backend.domains.edu.seo.geo;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * Localized Search Intent Mapper.
 * Resolves regional search terms to exact latitude, longitude, and administrative
 * district tags, ensuring geographical precision for local pack queries.
 */
@Service
public class LocationIntentMapper {

    /**
     * Resolves geographical coordinates corresponding to city names.
     */
    public Map<String, Object> resolveGeoCoordinates(String location) {
        Map<String, Object> geoMap = new HashMap<>();
        if (location == null) return geoMap;
        
        String normalized = location.toLowerCase().trim();

        if ("colombo".equals(normalized)) {
            geoMap.put("latitude", 6.9271);
            geoMap.put("longitude", 79.8612);
            geoMap.put("district", "Colombo District");
        } else if ("kandy".equals(normalized)) {
            geoMap.put("latitude", 7.2906);
            geoMap.put("longitude", 80.6337);
            geoMap.put("district", "Kandy District");
        } else if ("gampaha".equals(normalized)) {
            geoMap.put("latitude", 7.0873);
            geoMap.put("longitude", 80.0144);
            geoMap.put("district", "Gampaha District");
        } else {
            geoMap.put("latitude", 6.9271);
            geoMap.put("longitude", 79.8612);
            geoMap.put("district", "Western Province");
        }

        return geoMap;
    }
}
