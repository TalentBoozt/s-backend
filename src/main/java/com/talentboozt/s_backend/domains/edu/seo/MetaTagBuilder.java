package com.talentboozt.s_backend.domains.edu.seo;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class MetaTagBuilder {

    private static final String BASE_URL = "https://edu.talnova.io";

    /**
     * Builds OpenGraph dynamic meta tags mapping.
     */
    public Map<String, String> buildOpenGraphTags(String title, String description, String url, String type, String image) {
        Map<String, String> og = new HashMap<>();
        og.put("og:title", title != null ? title : "Talnova Education Platform");
        og.put("og:description", description != null ? description : "Modern skills development, remote job readiness, and future learning.");
        og.put("og:url", url != null ? url : BASE_URL);
        og.put("og:type", type != null ? type : "website");
        og.put("og:image", image != null ? image : BASE_URL + "/default-og.png");
        og.put("og:site_name", "Talnova");
        return og;
    }

    /**
     * Builds Twitter Card dynamic meta tags mapping.
     */
    public Map<String, String> buildTwitterCardTags(String title, String description, String image) {
        Map<String, String> twitter = new HashMap<>();
        twitter.put("twitter:card", "summary_large_image");
        twitter.put("twitter:title", title != null ? title : "Talnova Education Platform");
        twitter.put("twitter:description", description != null ? description : "Modern skills development, remote job readiness, and future learning.");
        twitter.put("twitter:image", image != null ? image : BASE_URL + "/default-og.png");
        return twitter;
    }
}
