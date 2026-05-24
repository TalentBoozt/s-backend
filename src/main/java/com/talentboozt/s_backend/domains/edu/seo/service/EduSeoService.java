package com.talentboozt.s_backend.domains.edu.seo.service;

import com.talentboozt.s_backend.domains.edu.seo.dto.SeoResponseDto;
import com.talentboozt.s_backend.domains.edu.seo.dto.HreflangEntryDto;
import com.talentboozt.s_backend.domains.edu.seo.model.CourseDocument;
import com.talentboozt.s_backend.domains.edu.seo.model.InstructorProfileDocument;
import com.talentboozt.s_backend.domains.edu.seo.repository.CourseSeoRepository;
import com.talentboozt.s_backend.domains.edu.seo.repository.InstructorSeoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Technical SEO Engine Service.
 * Manages dynamically compiled metadata configurations, XML course/profile sitemaps,
 * and robots routing directives in Spring Boot, pulling directly from MongoDB.
 */
@Service
public class EduSeoService {

    private final String BASE_URL = "https://edu.talnova.io";

    @Autowired
    private CourseSeoRepository courseRepository;

    @Autowired
    private InstructorSeoRepository instructorRepository;

    /**
     * Resolves metadata configurations, hreflang links, and JSON-LD schemas for a specific course.
     */
    public SeoResponseDto getCourseSeo(String slug) {
        String canonical = BASE_URL + "/course/" + slug.toLowerCase();
        Optional<CourseDocument> optionalCourse = courseRepository.findBySeoSlug(slug);

        if (optionalCourse.isPresent()) {
            CourseDocument course = optionalCourse.get();
            List<HreflangEntryDto> hreflangs = List.of(
                new HreflangEntryDto("en-LK", canonical),
                new HreflangEntryDto("si-LK", BASE_URL + "/si-LK/course/" + slug.toLowerCase()),
                new HreflangEntryDto("ta-LK", BASE_URL + "/ta-LK/course/" + slug.toLowerCase()),
                new HreflangEntryDto("x-default", canonical)
            );

            Map<String, String> openGraph = Map.of(
                "og:title", course.getSeoTitle() != null ? course.getSeoTitle() : "Course | Talnova",
                "og:description", course.getSeoDescription() != null ? course.getSeoDescription() : "Master subject tracks with certified instructors.",
                "og:url", canonical,
                "og:type", "video.course",
                "og:image", BASE_URL + "/api/v1/edu/seo/og/course/" + slug.toLowerCase()
            );

            return new SeoResponseDto(
                course.getSeoTitle(),
                course.getSeoDescription(),
                canonical,
                hreflangs,
                openGraph,
                course.getSchemaJsonLd(),
                course.getIndexable() ? "index, follow" : "noindex, nofollow",
                course.getSeoKeywords()
            );
        }

        // Legacy Fallback default
        List<HreflangEntryDto> hreflangs = List.of(
            new HreflangEntryDto("en-LK", canonical),
            new HreflangEntryDto("si-LK", BASE_URL + "/si-LK/course/" + slug.toLowerCase()),
            new HreflangEntryDto("ta-LK", BASE_URL + "/ta-LK/course/" + slug.toLowerCase()),
            new HreflangEntryDto("x-default", canonical)
        );

        Map<String, String> openGraph = Map.of(
            "og:title", "G.C.E. A/L Combined Mathematics | Talnova",
            "og:description", "Comprehensive Combined Maths curriculum classes aligned with local Sri Lankan syllabus schedules.",
            "og:url", canonical,
            "og:type", "video.course",
            "og:image", BASE_URL + "/default-og.png"
        );

        return new SeoResponseDto(
            "Combined Mathematics Revision | Talnova",
            "Comprehensive Combined Maths curriculum classes aligned with local Sri Lankan syllabus schedules.",
            canonical,
            hreflangs,
            openGraph,
            "{}",
            "index, follow",
            "Combined Maths, Sri Lanka syllabus revision"
        );
    }

    /**
     * Resolves metadata configurations, hreflang links, and JSON-LD schemas for an instructor profile.
     */
    public SeoResponseDto getProfileSeo(String slug) {
        String canonical = BASE_URL + "/p/" + slug.toLowerCase();
        Optional<InstructorProfileDocument> optionalProfile = instructorRepository.findBySeoSlug(slug);

        if (optionalProfile.isPresent()) {
            InstructorProfileDocument profile = optionalProfile.get();
            List<HreflangEntryDto> hreflangs = List.of(
                new HreflangEntryDto("en-LK", canonical),
                new HreflangEntryDto("si-LK", BASE_URL + "/si-LK/p/" + slug.toLowerCase()),
                new HreflangEntryDto("ta-LK", BASE_URL + "/ta-LK/p/" + slug.toLowerCase()),
                new HreflangEntryDto("x-default", canonical)
            );

            Map<String, String> openGraph = Map.of(
                "og:title", "Educator Profile | Talnova",
                "og:description", profile.getAiSummary() != null ? profile.getAiSummary() : "Explore verified course guides.",
                "og:url", canonical,
                "og:type", "profile",
                "og:image", BASE_URL + "/default-og.png"
            );

            return new SeoResponseDto(
                "Educator Profile | Talnova",
                profile.getAiSummary(),
                canonical,
                hreflangs,
                openGraph,
                profile.getSchemaJsonLd(),
                profile.getIndexable() ? "index, follow" : "noindex, nofollow",
                "Educator, Talnova teacher"
            );
        }

        List<HreflangEntryDto> hreflangs = List.of(
            new HreflangEntryDto("en-LK", canonical),
            new HreflangEntryDto("si-LK", BASE_URL + "/si-LK/p/" + slug.toLowerCase()),
            new HreflangEntryDto("ta-LK", BASE_URL + "/ta-LK/p/" + slug.toLowerCase()),
            new HreflangEntryDto("x-default", canonical)
        );

        Map<String, String> openGraph = Map.of(
            "og:title", "Educator Profile | Talnova",
            "og:description", "Explore verified courses and schedules from experienced platform teachers.",
            "og:url", canonical,
            "og:type", "profile",
            "og:image", BASE_URL + "/default-og.png"
        );

        return new SeoResponseDto(
            "Educator Profile | Talnova",
            "Explore verified courses and schedules from experienced platform teachers.",
            canonical,
            hreflangs,
            openGraph,
            "{}",
            "index, follow",
            "Educator, Teacher, Talnova classes"
        );
    }

    /**
     * Compiles dynamic XML sitemap of all active public courses from MongoDB.
     */
    public String getCourseSitemapXml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        List<CourseDocument> courses = courseRepository.findAllIndexableProjections();
        String today = java.time.LocalDate.now().toString();

        if (courses.isEmpty()) {
            sb.append("  <url>\n");
            sb.append("    <loc>").append(BASE_URL).append("/course/al-physics-theory</loc>\n");
            sb.append("    <changefreq>daily</changefreq>\n");
            sb.append("    <priority>0.8</priority>\n");
            sb.append("    <lastmod>").append(today).append("</lastmod>\n");
            sb.append("  </url>\n");
        } else {
            for (CourseDocument course : courses) {
                String lastmod = course.getUpdatedAt() != null 
                        ? course.getUpdatedAt().toInstant().toString().substring(0, 10) 
                        : today;
                sb.append("  <url>\n");
                sb.append("    <loc>").append(BASE_URL).append("/course/").append(course.getSeoSlug()).append("</loc>\n");
                sb.append("    <changefreq>daily</changefreq>\n");
                sb.append("    <priority>0.8</priority>\n");
                sb.append("    <lastmod>").append(lastmod).append("</lastmod>\n");
                sb.append("  </url>\n");
            }
        }

        sb.append("</urlset>");
        return sb.toString();
    }

    /**
     * Compiles dynamic XML sitemap of all active public instructor profiles from MongoDB.
     */
    public String getProfileSitemapXml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        List<InstructorProfileDocument> profiles = instructorRepository.findAllIndexableProjections();
        String today = java.time.LocalDate.now().toString();

        if (profiles.isEmpty()) {
            sb.append("  <url>\n");
            sb.append("    <loc>").append(BASE_URL).append("/p/dr-nishantha-kumara</loc>\n");
            sb.append("    <changefreq>weekly</changefreq>\n");
            sb.append("    <priority>0.6</priority>\n");
            sb.append("    <lastmod>").append(today).append("</lastmod>\n");
            sb.append("  </url>\n");
        } else {
            for (InstructorProfileDocument profile : profiles) {
                sb.append("  <url>\n");
                sb.append("    <loc>").append(BASE_URL).append("/p/").append(profile.getSeoSlug()).append("</loc>\n");
                sb.append("    <changefreq>weekly</changefreq>\n");
                sb.append("    <priority>0.6</priority>\n");
                sb.append("    <lastmod>").append(today).append("</lastmod>\n");
                sb.append("  </url>\n");
            }
        }

        sb.append("</urlset>");
        return sb.toString();
    }

    /**
     * Compiles robots.txt mapping standard crawler limits and pointing to sitemap indexes.
     */
    public String getRobotsTxt() {
        return """
        # ==========================================
        # Dynamic Robots.txt for Talnova Education Platform
        # ==========================================
        
        User-agent: *
        Disallow: /learner/
        Disallow: /creator/
        Disallow: /admin/
        Disallow: /checkout/
        Disallow: /cart
        Disallow: /api/
        Allow: /
        Allow: /explore
        Allow: /course/
        Allow: /p/
        Allow: /faq
        Allow: /pricing
        
        Sitemap: %s/sitemap_index.xml
        """.formatted(BASE_URL);
    }
}
