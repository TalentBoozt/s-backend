package com.talentboozt.s_backend.domains.edu.seo.controller;

import com.talentboozt.s_backend.domains.edu.seo.dto.SeoResponseDto;
import com.talentboozt.s_backend.domains.edu.seo.SeoService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller exposing technical SEO hooks.
 * Serves dynamic course and profile sitemaps, robots routing directives,
 * and page-level metadata JSON blocks to both edge proxies and frontend clients.
 */
@RestController
@CrossOrigin(originPatterns = "*")
public class EduSeoController {

    private final SeoService seoService;
    private final com.talentboozt.s_backend.domains.edu.seo.sitemap.SitemapService sitemapService;

    public EduSeoController(SeoService seoService, com.talentboozt.s_backend.domains.edu.seo.sitemap.SitemapService sitemapService) {
        this.seoService = seoService;
        this.sitemapService = sitemapService;
    }

    /**
     * Dynamic SEO API Delivery endpoint allowing the SPA to fetch pre-computed metadata.
     */
    @GetMapping("/api/v1/edu/seo/resolve")
    public ResponseEntity<SeoResponseDto> resolveSeo(@RequestParam String route) {
        String cleanRoute = route.startsWith("/") ? route.substring(1) : route;
        String[] parts = cleanRoute.split("/");
        
        String pageType = "landing";
        String slug = cleanRoute;
        
        if (parts.length >= 2) {
            String segment1 = parts[0].toLowerCase();
            if ("course".equals(segment1) || "courses".equals(segment1)) {
                pageType = "course";
                slug = parts[1];
            } else if ("p".equals(segment1) || "profile".equals(segment1) || "instructor".equals(segment1)) {
                pageType = "profile";
                slug = parts[1];
            } else if ("category".equals(segment1) || "categories".equals(segment1)) {
                pageType = "category";
                slug = parts[1];
            } else if ("materials".equals(segment1)) {
                pageType = "landing";
                slug = parts[1];
            } else if ("institute".equals(segment1)) {
                pageType = "institute";
                slug = parts[1];
            }
        }
        
        return ResponseEntity.ok(seoService.getSeoMetadata(pageType, slug, new java.util.HashMap<>()));
    }

    /**
     * Resolves custom SEO metadata, hreflangs, and schemas for dynamic courses.
     */
    @GetMapping("/api/v1/edu/seo/course/{slug}")
    public ResponseEntity<SeoResponseDto> getCourseSeo(@PathVariable String slug) {
        return ResponseEntity.ok(seoService.getCourseSeo(slug));
    }

    /**
     * Resolves custom SEO metadata, hreflangs, and schemas for instructor profiles.
     */
    @GetMapping("/api/v1/edu/seo/profile/{slug}")
    public ResponseEntity<SeoResponseDto> getProfileSeo(@PathVariable String slug) {
        return ResponseEntity.ok(seoService.getProfileSeo(slug));
    }

    /**
     * Live-compiles and returns the sitemap index.
     */
    @GetMapping(value = "/sitemap_index.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getSitemapIndex() {
        return ResponseEntity.ok(sitemapService.getSitemapIndex());
    }

    /**
     * Live-compiles and returns the course dynamic sitemap.
     */
    @GetMapping(value = "/sitemap_courses.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getCourseSitemap() {
        return ResponseEntity.ok(sitemapService.getSitemap("courses"));
    }

    /**
     * Live-compiles and returns the materials sitemap.
     */
    @GetMapping(value = "/sitemap_materials.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getMaterialsSitemap() {
        return ResponseEntity.ok(sitemapService.getSitemap("materials"));
    }

    /**
     * Live-compiles and returns the categories sitemap.
     */
    @GetMapping(value = "/sitemap_categories.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getCategoriesSitemap() {
        return ResponseEntity.ok(sitemapService.getSitemap("categories"));
    }

    /**
     * Live-compiles and returns the static pages sitemap.
     */
    @GetMapping(value = "/sitemap_static.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getStaticSitemap() {
        return ResponseEntity.ok(sitemapService.getSitemap("static"));
    }

    /**
     * Live-compiles and returns robots routing configurations.
     */
    @GetMapping(value = "/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getRobotsTxt() {
        return ResponseEntity.ok(seoService.getRobotsTxt());
    }

    /**
     * Serves dynamic llms.txt protocol to AI crawlers.
     */
    @GetMapping(value = {"/llms.txt", "/.well-known/llms.txt"}, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getLlmsTxt() {
        return ResponseEntity.ok(seoService.getLlmsTxt());
    }
}
