package com.talentboozt.s_backend.domains.edu.seo.geo;

import org.springframework.stereotype.Service;

/**
 * Localized District SEO Service.
 * Compiles custom text block outlines for targeted provincial and district hubs.
 */
@Service
public class DistrictSeoService {

    /**
     * Builds highly targetable localized descriptive copies.
     */
    public String generateDistrictSeoCopy(String district, String subject) {
        String normalizedDistrict = (district != null) ? district : "Sri Lanka";
        String normalizedSubject = (subject != null) ? subject : "All Subjects";
        
        return "Providing top-ranked G.C.E. Advanced Level " + normalizedSubject + 
               " tuition centers and online lecturers across " + normalizedDistrict + 
               ", Sri Lanka. Find certified high-performance revision hubs near you.";
    }
}
