package com.talentboozt.s_backend.domains.edu.seo;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SchemaMarkupService {

    /**
     * Generates a dynamic Course JSON-LD schema block.
     */
    public String generateCourseSchema(String id, String name, String description, String providerName, String providerUrl) {
        return """
        {
          "@context": "https://schema.org",
          "@type": "Course",
          "@id": "%s",
          "name": "%s",
          "description": "%s",
          "provider": {
            "@type": "EducationalOrganization",
            "name": "%s",
            "sameAs": "%s"
          }
        }
        """.formatted(id, name, description, providerName, providerUrl);
    }

    /**
     * Generates a BreadcrumbList JSON-LD schema.
     */
    public String generateBreadcrumbSchema(List<String[]> crumbs) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n  \"@context\": \"https://schema.org\",\n  \"@type\": \"BreadcrumbList\",\n  \"itemListElement\": [\n");
        for (int i = 0; i < crumbs.size(); i++) {
            String[] crumb = crumbs.get(i);
            sb.append("    {\n");
            sb.append("      \"@type\": \"ListItem\",\n");
            sb.append("      \"position\": ").append(i + 1).append(",\n");
            sb.append("      \"name\": \"").append(crumb[0]).append("\",\n");
            sb.append("      \"item\": \"").append(crumb[1]).append("\"\n");
            sb.append("    }");
            if (i < crumbs.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("  ]\n}");
        return sb.toString();
    }

    /**
     * Generates dynamic Organization schema.
     */
    public String generateOrganizationSchema() {
        return """
        {
          "@context": "https://schema.org",
          "@type": "EducationalOrganization",
          "name": "Talnova",
          "url": "https://edu.talnova.io",
          "logo": "https://edu.talnova.io/logo.png",
          "sameAs": [
            "https://facebook.com/talnova",
            "https://linkedin.com/company/talnova"
          ]
        }
        """;
    }

    /**
     * Generates a unified Website with SearchAction schema block for the homepage.
     */
    public String generateHomepageSchema() {
        return """
        {
          "@context": "https://schema.org",
          "@graph": [
            {
              "@type": "WebSite",
              "@id": "https://edu.talnova.io/#website",
              "url": "https://edu.talnova.io",
              "name": "Talnova Education",
              "description": "High-authority dynamic educational ecosystem for future-ready skills, remote careers, and practical AI classes.",
              "potentialAction": {
                "@type": "SearchAction",
                "target": {
                  "@type": "EntryPoint",
                  "urlTemplate": "https://edu.talnova.io/explore?q={search_term_string}"
                },
                "query-input": "required name=search_term_string"
              }
            },
            {
              "@type": "EducationalOrganization",
              "@id": "https://edu.talnova.io/#organization",
              "name": "Talnova",
              "url": "https://edu.talnova.io",
              "logo": "https://edu.talnova.io/logo.png",
              "sameAs": [
                "https://facebook.com/talnova",
                "https://linkedin.com/company/talnova"
              ]
            }
          ]
        }
        """;
    }

    /**
     * Generates an ItemList JSON-LD schema block.
     */
    public String generateItemListSchema(String listName, List<String[]> items) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n  \"@context\": \"https://schema.org\",\n  \"@type\": \"ItemList\",\n  \"name\": \"").append(listName).append("\",\n  \"itemListElement\": [\n");
        for (int i = 0; i < items.size(); i++) {
            String[] item = items.get(i);
            sb.append("    {\n");
            sb.append("      \"@type\": \"ListItem\",\n");
            sb.append("      \"position\": ").append(i + 1).append(",\n");
            sb.append("      \"name\": \"").append(item[0]).append("\",\n");
            sb.append("      \"url\": \"").append(item[1]).append("\"\n");
            sb.append("    }");
            if (i < items.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("  ]\n}");
        return sb.toString();
    }
}
