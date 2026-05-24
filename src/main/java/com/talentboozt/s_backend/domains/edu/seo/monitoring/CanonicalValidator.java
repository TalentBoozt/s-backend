package com.talentboozt.s_backend.domains.edu.seo.monitoring;

import org.springframework.stereotype.Service;

/**
 * Technical Canonical URL Validator.
 * Validates canonical URL paths, ensuring absolute targets conform to HTTPS domains.
 */
@Service
public class CanonicalValidator {

    /**
     * Asserts if canonical parameters contain tracking links or relative paths.
     */
    public boolean isCanonicalUrlValid(String canonicalUrl) {
        if (canonicalUrl == null) return false;
        return canonicalUrl.startsWith("https://edu.talnova.io/") && 
               !canonicalUrl.contains("?") && 
               !canonicalUrl.contains("&");
    }
}
