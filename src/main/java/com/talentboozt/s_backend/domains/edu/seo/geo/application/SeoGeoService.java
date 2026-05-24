package com.talentboozt.s_backend.domains.edu.seo.geo.application;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class SeoGeoService {

    public Map<String, Object> mapGeoIntent(String locationName, String courseTitle) {
        Map<String, Object> geoMap = new HashMap<>();
        geoMap.put("location", locationName);
        geoMap.put("course", courseTitle);
        geoMap.put("seoTitle", "Best Premium " + courseTitle + " Courses in " + locationName + " | Talnova");
        geoMap.put("schemaMarkup", "{\"@context\":\"https://schema.org\",\"@type\":\"EducationalOrganization\",\"name\":\"Talnova " + locationName + "\" }");
        return geoMap;
    }
}
