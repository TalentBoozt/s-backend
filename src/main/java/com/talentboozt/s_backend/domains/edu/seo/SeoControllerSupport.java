package com.talentboozt.s_backend.domains.edu.seo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SeoControllerSupport {

    @Autowired
    private SeoService seoService;

    /**
     * Resolves dynamic SEO metadata based on parameters list and request scope
     */
    public Map<String, Object> compileSeoResponse(String pageType, String slug, Map<String, Object> additionalContext) {
        Map<String, Object> context = new HashMap<>();
        if (additionalContext != null) {
            context.putAll(additionalContext);
        }
        
        var seoResponse = seoService.getSeoMetadata(pageType, slug, context);
        
        Map<String, Object> response = new HashMap<>();
        response.put("title", seoResponse.title());
        response.put("description", seoResponse.description());
        response.put("canonical", seoResponse.canonical());
        response.put("hreflangs", seoResponse.hreflangs());
        response.put("openGraph", seoResponse.openGraph());
        response.put("schema", seoResponse.schema());
        response.put("robots", seoResponse.robots());
        response.put("keywords", seoResponse.keywords());
        
        return response;
    }
}
