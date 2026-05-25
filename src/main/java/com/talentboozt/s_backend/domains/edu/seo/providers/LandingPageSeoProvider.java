package com.talentboozt.s_backend.domains.edu.seo.providers;

import com.talentboozt.s_backend.domains.edu.model.EProgrammaticPage;
import com.talentboozt.s_backend.domains.edu.programmatic.ProgrammaticPageRepository;
import com.talentboozt.s_backend.domains.edu.seo.DynamicSeoFactory;
import com.talentboozt.s_backend.domains.edu.seo.SeoMetadata;
import com.talentboozt.s_backend.domains.edu.seo.SeoTemplateEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Optional;

@Component
public class LandingPageSeoProvider implements SeoProvider {

    @Autowired
    private ProgrammaticPageRepository programmaticRepository;

    @Autowired
    private SeoTemplateEngine templateEngine;

    @Override
    public boolean supports(String pageType, Map<String, Object> context) {
        return "landing".equalsIgnoreCase(pageType) || "programmatic".equalsIgnoreCase(pageType);
    }

    @Override
    public SeoMetadata generate(String slug, Map<String, Object> context) {
        Optional<EProgrammaticPage> optionalPage = programmaticRepository.findBySlug(slug);

        if (optionalPage.isPresent()) {
            EProgrammaticPage page = optionalPage.get();
            String title = page.getSeoTitle() != null ? page.getSeoTitle() : page.getTitle();
            String description = page.getSeoDescription() != null ? page.getSeoDescription() : "Scale-generated educational landing page.";
            String keywords = (page.getSemanticKeywords() != null) ? String.join(", ", page.getSemanticKeywords()) : "future skills, AI, software engineering, Talnova";

            SeoMetadata metadata = DynamicSeoFactory.createBaseSeo("/" + slug, title, description, keywords);
            metadata.setRobotsDirectives(page.getIndexable() ? "index, follow" : "noindex, nofollow");
            
            return metadata;
        }

        // Reposition fallback landing pages toward modern educational discovery
        String title = templateEngine.renderTitle("Professional Skills, AI Courses & Career Development Platform | Talnova", Map.of());
        String description = templateEngine.renderDescription("Empower your career with modern digital skills. Explore high-converting courses in Generative AI, remote work freelancing, react/web development, digital marketing, and advanced creative arts.", Map.of());

        return DynamicSeoFactory.createBaseSeo(
            "/" + slug,
            title,
            description,
            "professional skills, future skills, AI courses, freelancing online income, React tech, digital marketing, startups entrepreneurship"
        );
    }
}
