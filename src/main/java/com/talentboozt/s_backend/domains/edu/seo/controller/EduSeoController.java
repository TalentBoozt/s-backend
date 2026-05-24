package com.talentboozt.s_backend.domains.edu.seo.controller;

import com.talentboozt.s_backend.domains.edu.seo.dto.SeoResponseDto;
import com.talentboozt.s_backend.domains.edu.seo.service.EduSeoService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller exposing technical SEO hooks.
 * Serves dynamic course and profile sitemaps, robots routing directives,
 * and page-level metadata JSON blocks to both edge proxies and frontend clients.
 */
@RestController
@CrossOrigin(origins = "*")
public class EduSeoController {

    private final EduSeoService seoService;

    public EduSeoController(EduSeoService seoService) {
        this.seoService = seoService;
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
     * Live-compiles and returns the course dynamic sitemap.
     */
    @GetMapping(value = "/sitemap_courses.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getCourseSitemap() {
        return ResponseEntity.ok(seoService.getCourseSitemapXml());
    }

    /**
     * Live-compiles and returns the instructor dynamic sitemap.
     */
    @GetMapping(value = "/sitemap_profiles.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getProfileSitemap() {
        return ResponseEntity.ok(seoService.getProfileSitemapXml());
    }

    /**
     * Live-compiles and returns robots routing configurations.
     */
    @GetMapping(value = "/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getRobotsTxt() {
        return ResponseEntity.ok(seoService.getRobotsTxt());
    }
}
