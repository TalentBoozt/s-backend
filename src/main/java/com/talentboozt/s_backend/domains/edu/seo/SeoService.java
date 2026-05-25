package com.talentboozt.s_backend.domains.edu.seo;

import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.model.EProfiles;
import com.talentboozt.s_backend.domains.edu.seo.dto.SeoResponseDto;
import com.talentboozt.s_backend.domains.edu.seo.repository.CourseSeoRepository;
import com.talentboozt.s_backend.domains.edu.seo.repository.InstructorSeoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SeoService {

    private final String BASE_URL = "https://edu.talnova.io";

    @Autowired
    private SeoResolver seoResolver;

    @Autowired
    private CourseSeoRepository courseRepository;

    @Autowired
    private InstructorSeoRepository instructorRepository;

    // Fast-access dynamic SEO cache to bypass DB trips for repeated requests (e.g., crawlers)
    private final Map<String, SeoResponseDto> seoCache = new ConcurrentHashMap<>();

    public SeoResponseDto getSeoMetadata(String pageType, String slug, Map<String, Object> context) {
        String cacheKey = String.format("%s:%s:%s", pageType, slug != null ? slug.toLowerCase() : "", context != null ? context.toString() : "empty");
        
        return seoCache.computeIfAbsent(cacheKey, key -> {
            SeoMetadata metadata = seoResolver.resolveMetadata(pageType, slug, context);
            return new SeoResponseDto(
                metadata.getTitle(),
                metadata.getDescription(),
                metadata.getCanonicalUrl(),
                metadata.getHreflangs(),
                metadata.getOpenGraph(),
                metadata.getSchemaJsonLd(),
                metadata.getRobotsDirectives(),
                metadata.getKeywords()
            );
        });
    }

    public SeoResponseDto getCourseSeo(String slug) {
        return getSeoMetadata("course", slug, new HashMap<>());
    }

    public SeoResponseDto getProfileSeo(String slug) {
        return getSeoMetadata("profile", slug, new HashMap<>());
    }

    public void evictCache() {
        seoCache.clear();
        System.out.println("[SeoService] Dynamic SEO metadata cache cleared.");
    }

    public String getCourseSitemapXml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        List<ECourses> courses = courseRepository.findAllIndexableProjections();
        String today = java.time.LocalDate.now().toString();

        if (courses.isEmpty()) {
            sb.append("  <url>\n");
            sb.append("    <loc>").append(BASE_URL).append("/course/al-physics-theory</loc>\n");
            sb.append("    <changefreq>daily</changefreq>\n");
            sb.append("    <priority>0.8</priority>\n");
            sb.append("    <lastmod>").append(today).append("</lastmod>\n");
            sb.append("  </url>\n");
        } else {
            for (ECourses course : courses) {
                String lastmod = course.getUpdatedAt() != null 
                        ? course.getUpdatedAt().toString().substring(0, 10) 
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

    public String getProfileSitemapXml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        List<EProfiles> profiles = instructorRepository.findAllIndexableProjections();
        String today = java.time.LocalDate.now().toString();

        if (profiles.isEmpty()) {
            sb.append("  <url>\n");
            sb.append("    <loc>").append(BASE_URL).append("/p/dr-nishantha-kumara</loc>\n");
            sb.append("    <changefreq>weekly</changefreq>\n");
            sb.append("    <priority>0.6</priority>\n");
            sb.append("    <lastmod>").append(today).append("</lastmod>\n");
            sb.append("  </url>\n");
        } else {
            for (EProfiles profile : profiles) {
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

    public String getRobotsTxt() {
        return """
        # ==========================================
        # Dynamic Robots.txt for Talnova Education Platform
        # Configured dynamically for Netlify Subdomains + Cloudflare CDN
        # ==========================================

        User-agent: *
        Allow: /
        Allow: /explore
        Allow: /course/
        Allow: /p/
        Allow: /materials/
        Allow: /category/

        # Block private dashboards and auth forms
        Disallow: /learner/
        Disallow: /creator/
        Disallow: /admin/
        Disallow: /login
        Disallow: /register
        Disallow: /checkout/

        # Dynamic Sitemaps Directives
        Sitemap: https://edu.talnova.io/sitemap_index.xml
        """;
    }

    public String getLlmsTxt() {
        StringBuilder sb = new StringBuilder();
        sb.append("# Talnova Education Platform\n");
        sb.append("## Dynamic AI-First Search Discoverability & Semantic Authority Hub\n\n");
        sb.append("> Talnova is a leading high-authority educational ecosystem focused on professional courses, future skills, career readiness, practical education, AI training, and remote freelancing preparation.\n\n");
        
        sb.append("## Platform URL Structures\n");
        sb.append("- Explore Categories: https://edu.talnova.io/explore\n");
        sb.append("- Tuition Subject Hubs: https://edu.talnova.io/explore?category=Strategy\n");
        sb.append("- Material Resources: https://edu.talnova.io/explore?category=Language\n\n");

        sb.append("## High-Density Search Indexes & Sitemaps\n");
        sb.append("- XML Sitemap Index: https://edu.talnova.io/sitemap_index.xml\n");
        sb.append("- Course Catalog Sitemap: https://edu.talnova.io/sitemap_courses.xml\n");
        sb.append("- Materials & Downloads Sitemap: https://edu.talnova.io/sitemap_materials.xml\n");
        sb.append("- Category Hubs Sitemap: https://edu.talnova.io/sitemap_categories.xml\n\n");

        sb.append("## Top High-Authority Career Tracks\n");
        sb.append("- AI & Automation Academy: Practical Prompt Engineering, automation pipelines, and modern AI tools.\n");
        sb.append("- Freelancing & Remote Work: Building portfolios, landing remote gigs, global freelancing frameworks.\n");
        sb.append("- Software Development: Front-end engineering, back-end web development, cloud tools.\n");
        sb.append("- Business Strategy & Creative Skills: Outlining target demographics, UI/UX designing, corporate growth.\n\n");

        sb.append("## LLM Crawling & Directives\n");
        sb.append("AI bots (GPTBot, ClaudeBot, PerplexityBot, OAI-SearchBot) are fully permitted to extract structural JSON-LD schemas, breadcrumb trails, and dynamically generated FAQ lists to provide accurate educational references and rich citation highlights.");
        return sb.toString();
    }
}
