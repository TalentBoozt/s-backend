package com.talentboozt.s_backend.domains.edu.seo.geo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * Technical Local GeoEntity Schema Service.
 * Compiles complex Schema.org structures combining EducationalOrganization, Place,
 * and GeoCoordinates metadata to capture map placements.
 */
@Service
public class GeoEntitySchemaService {

    @Autowired
    private SchoolLocatorService schoolLocatorService;

    /**
     * Packages campus details into local organization schema models.
     */
    public Map<String, Object> compileGeoEntitySchema(String schoolName) {
        Map<String, Object> schoolMetrics = schoolLocatorService.resolveSchoolMetrics(schoolName);
        
        Map<String, Object> schema = new HashMap<>();
        schema.put("@context", "https://schema.org");
        schema.put("@type", "EducationalOrganization");
        schema.put("name", schoolMetrics.get("name"));
        
        Map<String, Object> address = new HashMap<>();
        address.put("@type", "PostalAddress");
        address.put("addressLocality", schoolMetrics.get("district"));
        address.put("addressCountry", "LK");
        schema.put("address", address);

        Map<String, Object> geoCoordinates = new HashMap<>();
        geoCoordinates.put("@type", "GeoCoordinates");
        geoCoordinates.put("latitude", schoolMetrics.get("latitude"));
        geoCoordinates.put("longitude", schoolMetrics.get("longitude"));
        
        Map<String, Object> locationPlace = new HashMap<>();
        locationPlace.put("@type", "Place");
        locationPlace.put("geo", geoCoordinates);
        schema.put("location", locationPlace);

        return schema;
    }
}
