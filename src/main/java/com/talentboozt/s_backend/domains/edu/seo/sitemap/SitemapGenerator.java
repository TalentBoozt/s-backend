package com.talentboozt.s_backend.domains.edu.seo.sitemap;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SitemapGenerator {

    /**
     * Builds standard compliant XML sitemap from SitemapUrl entities.
     */
    public String generateXml(List<SitemapUrl> urls) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"\n");
        sb.append("        xmlns:image=\"http://www.google.com/schemas/sitemap-image/1.1\">\n");

        for (SitemapUrl url : urls) {
            sb.append("  <url>\n");
            sb.append("    <loc>").append(escapeXml(url.getLoc())).append("</loc>\n");
            sb.append("    <lastmod>").append(url.getLastmod()).append("</lastmod>\n");
            sb.append("    <changefreq>").append(url.getChangefreq()).append("</changefreq>\n");
            sb.append("    <priority>").append(url.getPriority()).append("</priority>\n");
            
            if (url.getImages() != null && !url.getImages().isEmpty()) {
                for (String img : url.getImages()) {
                    sb.append("    <image:image>\n");
                    sb.append("      <image:loc>").append(escapeXml(img)).append("</image:loc>\n");
                    sb.append("    </image:image>\n");
                }
            }
            
            sb.append("  </url>\n");
        }

        sb.append("</urlset>");
        return sb.toString();
    }

    private String escapeXml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                    .replace("\"", "&quot;")
                    .replace("'", "&apos;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");
    }
}
