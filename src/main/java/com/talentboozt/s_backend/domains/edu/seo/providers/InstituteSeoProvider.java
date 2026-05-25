package com.talentboozt.s_backend.domains.edu.seo.providers;

import com.talentboozt.s_backend.domains.edu.seo.DynamicSeoFactory;
import com.talentboozt.s_backend.domains.edu.seo.SeoMetadata;
import com.talentboozt.s_backend.domains.edu.seo.SeoTemplateEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class InstituteSeoProvider implements SeoProvider {

    @Autowired
    private SeoTemplateEngine templateEngine;

    @Override
    public boolean supports(String pageType, Map<String, Object> context) {
        return "institute".equalsIgnoreCase(pageType);
    }

    @Override
    public SeoMetadata generate(String slug, Map<String, Object> context) {
        String cleanSlug = (slug != null) ? slug.replace("-", " ") : "institute";
        String capitalized = cleanSlug.isEmpty() ? "Expert" : cleanSlug.substring(0, 1).toUpperCase() + cleanSlug.substring(1);

        String title = templateEngine.renderTitle("{instituteName} Professional Tech Academy | Talnova", Map.of("instituteName", capitalized));
        String description = templateEngine.renderDescription("Acquire future-ready career skills online with {instituteName}. Explore professional masterclasses, industry-certified courses, and active student schedules.", Map.of("instituteName", capitalized));
        String keywords = capitalized + ", professional academy, career institute, technology school, modern skills";

        return DynamicSeoFactory.createBaseSeo("/institute/" + slug.toLowerCase(), title, description, keywords);
    }
}
