package com.talentboozt.s_backend.domains.edu.seo.providers;

import com.talentboozt.s_backend.domains.edu.seo.DynamicSeoFactory;
import com.talentboozt.s_backend.domains.edu.seo.SeoMetadata;
import com.talentboozt.s_backend.domains.edu.seo.SeoTemplateEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CategorySeoProvider implements SeoProvider {

    @Autowired
    private SeoTemplateEngine templateEngine;

    @Override
    public boolean supports(String pageType, Map<String, Object> context) {
        return "category".equalsIgnoreCase(pageType);
    }

    @Override
    public SeoMetadata generate(String slug, Map<String, Object> context) {
        String cleanSlug = (slug != null) ? slug.replace("-", " ") : "education";
        String capitalized = cleanSlug.isEmpty() ? "Expert" : cleanSlug.substring(0, 1).toUpperCase() + cleanSlug.substring(1);

        String title = templateEngine.renderTitle("Best {category} Courses, Tutorials and Career Materials | Talnova", Map.of("category", capitalized));
        String description = templateEngine.renderDescription("Discover the absolute best professional {category} courses, guides, and career launchpads. Master digital and practical skills to prepare for high-paying remote roles.", Map.of("category", capitalized));
        String keywords = capitalized + ", professional courses, modern skills, online bootcamps, career guides, Talnova";

        SeoMetadata metadata = DynamicSeoFactory.createBaseSeo("/category/" + slug.toLowerCase(), title, description, keywords);
        
        // Handle pagination parameters in searchParams context
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
                metadata.setTitle(title + " - Page " + page);
                metadata.setDescription(description + " (Page " + page + ")");
            }
        }

        return metadata;
    }
}
