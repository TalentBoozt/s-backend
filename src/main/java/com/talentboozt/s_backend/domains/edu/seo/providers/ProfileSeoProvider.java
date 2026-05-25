package com.talentboozt.s_backend.domains.edu.seo.providers;

import com.talentboozt.s_backend.domains.edu.model.EProfiles;
import com.talentboozt.s_backend.domains.edu.seo.DynamicSeoFactory;
import com.talentboozt.s_backend.domains.edu.seo.SeoMetadata;
import com.talentboozt.s_backend.domains.edu.seo.SeoTemplateEngine;
import com.talentboozt.s_backend.domains.edu.seo.repository.InstructorSeoRepository;
import com.talentboozt.s_backend.domains.edu.seo.faq.ProgrammaticFaqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Optional;

@Component
public class ProfileSeoProvider implements SeoProvider {

    @Autowired
    private InstructorSeoRepository instructorRepository;

    @Autowired
    private SeoTemplateEngine templateEngine;

    @Autowired
    private ProgrammaticFaqService faqService;

    @Override
    public boolean supports(String pageType, Map<String, Object> context) {
        return "profile".equalsIgnoreCase(pageType) || "instructor".equalsIgnoreCase(pageType);
    }

    @Override
    public SeoMetadata generate(String slug, Map<String, Object> context) {
        Optional<EProfiles> optionalProfile = instructorRepository.findBySeoSlug(slug);

        if (optionalProfile.isPresent()) {
            EProfiles profile = optionalProfile.get();
            
            String name = slug.replace("-", " ");
            String capitalizedName = name.isEmpty() ? "Expert" : name.substring(0, 1).toUpperCase() + name.substring(1);
            
            String title = templateEngine.renderTitle("Verified Instructor {instructorName} | Professional Skills & AI Academy", Map.of("instructorName", capitalizedName));
            String description = profile.getAiSummary() != null ? profile.getAiSummary() : 
                templateEngine.renderDescription("Learn directly from verified industry expert {instructorName}. Explore professional courses, future skills bootcamps, and career coaching options on Talnova.", Map.of("instructorName", capitalizedName));
            
            SeoMetadata metadata = DynamicSeoFactory.createBaseSeo("/p/" + slug.toLowerCase(), title, description, "Educator, Talnova teacher, professional skills mentor");
            
            // Build dynamic Person schema block
            String personSchema = profile.getSchemaJsonLd();
            if (personSchema == null || personSchema.isBlank() || "{}".equals(personSchema)) {
                String fName = profile.getFirstName() != null ? profile.getFirstName() : "";
                String lName = profile.getLastName() != null ? profile.getLastName() : "";
                personSchema = """
                {
                  "@type": "Person",
                  "@id": "https://edu.talnova.io/p/%s",
                  "name": "%s",
                  "jobTitle": "%s",
                  "worksFor": {
                    "@type": "Organization",
                    "name": "%s"
                  },
                  "description": "%s"
                }
                """.formatted(
                    profile.getSeoSlug(),
                    (fName + " " + lName).trim(),
                    profile.getJobTitle() != null ? profile.getJobTitle() : "Verified Instructor",
                    profile.getCompany() != null ? profile.getCompany() : "Talnova Academy",
                    profile.getBio() != null ? profile.getBio().replace("\"", "\\\"") : ""
                );
            }

            // Generate programmatic FAQs
            var faqs = faqService.generateFaqs("profile", slug);
            String faqSchema = faqService.generateFaqSchemaJson(faqs);

            // Merge both elements under @graph
            metadata.setSchemaJsonLd(mergeSchemas(personSchema, faqSchema));
            metadata.setRobotsDirectives(profile.getIndexable() ? "index, follow" : "noindex, nofollow");
            
            metadata.getOpenGraph().put("og:type", "profile");
            
            return metadata;
        }

        // Fallback profile
        SeoMetadata fallback = DynamicSeoFactory.createBaseSeo(
            "/p/" + slug.toLowerCase(),
            "Verified Industry Educators & Mentors | Talnova",
            "Learn from verified software engineers, AI researchers, creative designers, and expert educators guiding you toward high-paying careers.",
            "Educator, Teacher, Talnova classes, professional mentors"
        );
        fallback.getOpenGraph().put("og:type", "profile");
        return fallback;
    }

    private String mergeSchemas(String personJson, String faqJson) {
        if (faqJson == null || faqJson.isBlank()) {
            return personJson;
        }
        return "{\n  \"@context\": \"https://schema.org\",\n  \"@graph\": [\n" + personJson + ",\n" + faqJson + "\n  ]\n}";
    }
}
