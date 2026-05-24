package com.talentboozt.s_backend.domains.edu.seo.geo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * Localized GEO Landing Page Service.
 * Coordinates local coordinates mapping, district copies, and models local pack
 * Schema.org LocalBusiness/EducationCenter definitions.
 */
@Service
public class GeoLandingPageService {

    @Autowired
    private LocationIntentMapper intentMapper;

    @Autowired
    private DistrictSeoService districtSeoService;

    /**
     * Compiles localized local search properties and schemas.
     */
    public Map<String, Object> compileGeoSeoMeta(String location, String subject) {
        Map<String, Object> geoMetadata = new HashMap<>();

        Map<String, Object> coordinates = intentMapper.resolveGeoCoordinates(location);
        String district = (String) coordinates.get("district");
        
        geoMetadata.put("description", districtSeoService.generateDistrictSeoCopy(district, subject));
        
        // Build Local Schema structures
        Map<String, Object> educationCenterSchema = new HashMap<>();
        educationCenterSchema.put("@context", "https://schema.org");
        educationCenterSchema.put("@type", "EducationCenter");
        educationCenterSchema.put("name", "Talnova " + location + " " + subject + " Revision Hub");
        
        Map<String, Object> addressSchema = new HashMap<>();
        addressSchema.put("@type", "PostalAddress");
        addressSchema.put("addressLocality", location);
        addressSchema.put("addressRegion", district);
        addressSchema.put("addressCountry", "LK");
        educationCenterSchema.put("address", addressSchema);

        Map<String, Object> geoCoordinatesSchema = new HashMap<>();
        geoCoordinatesSchema.put("@type", "GeoCoordinates");
        geoCoordinatesSchema.put("latitude", coordinates.get("latitude"));
        geoCoordinatesSchema.put("longitude", coordinates.get("longitude"));
        educationCenterSchema.put("geo", geoCoordinatesSchema);

        geoMetadata.put("placeSchema", educationCenterSchema);
        return geoMetadata;
    }
}
