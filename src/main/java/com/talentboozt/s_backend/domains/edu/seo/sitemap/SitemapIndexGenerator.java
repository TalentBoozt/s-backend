package com.talentboozt.s_backend.domains.edu.seo.sitemap;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SitemapIndexGenerator {

    private static final String BASE_URL = "https://edu.talnova.io";

    /**
     * Builds standard sitemap index pointing to all category files.
     */
    public String generateIndexXml(List<String> sitemapFiles) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
        String today = java.time.LocalDate.now().toString();

        for (String file : sitemapFiles) {
            sb.append("  <sitemap>\n");
            sb.append("    <loc>").append(BASE_URL).append("/").append(file).append("</loc>\n");
            sb.append("    <lastmod>").append(today).append("</lastmod>\n");
            sb.append("  </sitemap>\n");
        }

        sb.append("</sitemapindex>");
        return sb.toString();
    }
}
