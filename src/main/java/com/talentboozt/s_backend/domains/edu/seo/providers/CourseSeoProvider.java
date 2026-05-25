package com.talentboozt.s_backend.domains.edu.seo.providers;

import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.seo.DynamicSeoFactory;
import com.talentboozt.s_backend.domains.edu.seo.SeoMetadata;
import com.talentboozt.s_backend.domains.edu.seo.SeoTemplateEngine;
import com.talentboozt.s_backend.domains.edu.seo.SchemaMarkupService;
import com.talentboozt.s_backend.domains.edu.seo.repository.CourseSeoRepository;
import com.talentboozt.s_backend.domains.edu.seo.knowledgegraph.EntityGraphService;
import com.talentboozt.s_backend.domains.edu.seo.faq.ProgrammaticFaqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Optional;

@Component
public class CourseSeoProvider implements SeoProvider {

    @Autowired
    private CourseSeoRepository courseRepository;

    @Autowired
    private SeoTemplateEngine templateEngine;

    @Autowired
    private SchemaMarkupService schemaService;

    @Autowired
    private EntityGraphService entityGraphService;

    @Autowired
    private ProgrammaticFaqService faqService;

    @Override
    public boolean supports(String pageType, Map<String, Object> context) {
        return "course".equalsIgnoreCase(pageType);
    }

    @Override
    public SeoMetadata generate(String slug, Map<String, Object> context) {
        Optional<ECourses> optionalCourse = courseRepository.findBySeoSlug(slug);

        if (optionalCourse.isPresent()) {
            ECourses course = optionalCourse.get();
            
            // Reposition towards professional skills & career readiness
            String courseTitle = course.getSeoTitle();
            if (courseTitle == null || courseTitle.isBlank()) {
                Map<String, String> vars = Map.of(
                    "courseName", course.getSeoSlug().replace("-", " "),
                    "platform", "Talnova"
                );
                courseTitle = templateEngine.renderTitle("Learn {courseName} Online | {platform}", vars);
            }

            String courseDesc = course.getSeoDescription();
            if (courseDesc == null || courseDesc.isBlank()) {
                Map<String, String> vars = Map.of(
                    "courseName", course.getSeoSlug().replace("-", " "),
                    "platform", "Talnova"
                );
                courseDesc = templateEngine.renderDescription("Master {courseName} and acquire future-ready career skills. Enroll in certified professional classes at {platform}.", vars);
            }

            String keywords = course.getSeoKeywords() != null ? course.getSeoKeywords() : "professional courses, future skills, career development";

            SeoMetadata metadata = DynamicSeoFactory.createBaseSeo("/course/" + slug.toLowerCase(), courseTitle, courseDesc, keywords);
            
            // Assemble dynamic knowledge graph linking courses, skills, and tools
            String kgSchema = entityGraphService.compileCourseKnowledgeGraph(course, courseRepository.findAllIndexableProjections());
            
            // Generate programmatic route-aware Q&As
            var faqs = faqService.generateFaqs("course", slug);
            String faqSchema = faqService.generateFaqSchemaJson(faqs);
            
            // Unified nested graph output for AI search bots
            String unifiedSchema = mergeSchemas(kgSchema, faqSchema);
            metadata.setSchemaJsonLd(unifiedSchema);
            
            metadata.setRobotsDirectives(course.getIndexable() ? "index, follow" : "noindex, nofollow");
            
            metadata.getOpenGraph().put("og:type", "video.course");
            metadata.getOpenGraph().put("og:image", "https://edu.talnova.io/api/v1/edu/seo/og/course/" + slug.toLowerCase());
            
            return metadata;
        }

        // Professional Fallback course description
        SeoMetadata fallback = DynamicSeoFactory.createBaseSeo(
            "/course/" + slug.toLowerCase(),
            "Professional Skills & Career Development Courses | Talnova",
            "Accelerate your career journey with certified professional programs, remote job preparation workshops, and modern digital skills classes.",
            "professional skills, career readiness, digital courses"
        );
        fallback.getOpenGraph().put("og:type", "video.course");
        return fallback;
    }

    private String mergeSchemas(String graphJson, String faqJson) {
        if (faqJson == null || faqJson.isBlank()) {
            return graphJson;
        }
        return "{\n  \"@context\": \"https://schema.org\",\n  \"@graph\": [\n" + graphJson + ",\n" + faqJson + "\n  ]\n}";
    }
}
