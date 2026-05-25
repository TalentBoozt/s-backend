package com.talentboozt.s_backend.domains.edu.seo;

import com.talentboozt.s_backend.domains.edu.seo.dto.HreflangEntryDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CanonicalUrlService {

    private static final String BASE_URL = "https://edu.talnova.io";

    /**
     * Dynamically generates a canonical URL for the page path, preserving pagination when indexable.
     */
    public String buildCanonicalUrl(String path, Map<String, Object> context) {
        String cleanPath = (path != null && path.startsWith("/")) ? path : "/" + (path != null ? path : "");
        if (cleanPath.equals("/")) cleanPath = "";
        
        StringBuilder canonical = new StringBuilder(BASE_URL).append(cleanPath);
        
        if (context != null && context.containsKey("page")) {
            Object pageObj = context.get("page");
            int page = 1;
            if (pageObj instanceof Integer) {
                page = (Integer) pageObj;
            } else if (pageObj instanceof String) {
                try {
                    page = Integer.parseInt((String) pageObj);
                } catch (NumberFormatException ignored) {}
            }
            if (page > 1) {
                canonical.append("?page=").append(page);
            }
        }
        
        return canonical.toString();
    }

    /**
     * Generates structured multilingual alternate links for target crawlers.
     */
    public List<HreflangEntryDto> buildHreflangAlternateLinks(String path, Map<String, Object> context) {
        String cleanPath = (path != null && path.startsWith("/")) ? path : "/" + (path != null ? path : "");
        if (cleanPath.equals("/")) cleanPath = "";

        List<HreflangEntryDto> hreflangs = new ArrayList<>();
        String canonicalEn = BASE_URL + cleanPath;
        
        hreflangs.add(new HreflangEntryDto("en-LK", canonicalEn));
        hreflangs.add(new HreflangEntryDto("si-LK", BASE_URL + "/si-LK" + cleanPath));
        hreflangs.add(new HreflangEntryDto("ta-LK", BASE_URL + "/ta-LK" + cleanPath));
        hreflangs.add(new HreflangEntryDto("x-default", canonicalEn));
        
        return hreflangs;
    }
}
