package com.talentboozt.s_backend.domains.edu.seo.identity;

import org.springframework.stereotype.Service;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Centralized Entity Identity Consistency Service.
 * Standardizes naming conventions, normalizes taxonomy structures,
 * and maintains structural naming integrity for E-E-A-T discoverability.
 */
@Service
public class EntityIdentityService {

    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9\\s-]");
    private static final Pattern MULTI_SPACE = Pattern.compile("\\s+");
    private static final Pattern MULTI_HYPHEN = Pattern.compile("-+");

    /**
     * Normalizes a name or term to a clean title format.
     */
    public String normalizeName(String name) {
        if (name == null || name.isBlank()) return "Talnova Educator";
        String[] words = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) continue;
            sb.append(Character.toUpperCase(word.charAt(0)))
              .append(word.substring(1).toLowerCase(Locale.ROOT))
              .append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * Standardizes a category label to prevent duplicate taxonomies.
     */
    public String normalizeCategory(String category) {
        if (category == null || category.isBlank()) return "Career Readiness";
        String normalized = category.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "AI", "AI_AND_AUTOMATION", "ARTIFICIAL INTELLIGENCE" -> "AI & Automation";
            case "FREELANCE", "FREELANCING", "REMOTE_WORK" -> "Freelancing & Remote Careers";
            case "DEV", "SOFTWARE", "SOFTWARE_DEVELOPMENT", "PROGRAMMING" -> "Software Development";
            case "MARKETING", "DIGITAL_MARKETING" -> "Digital Marketing";
            case "CREATIVE", "CREATIVE_SKILLS", "DESIGN" -> "Creative Skills & Design";
            case "BIZ", "BUSINESS" -> "Business & Strategy";
            case "ACADEMICS", "ACADEMIC_SUPPORT", "EXAM_PREP" -> "Academic & Exam Preparation";
            default -> normalizeName(category);
        };
    }

    /**
     * Generates a unique, URL-safe SEO slug.
     */
    public String generateSafeSlug(String title) {
        if (title == null || title.isBlank()) {
            return "item-" + java.util.UUID.randomUUID().toString().substring(0, 8);
        }
        String normalized = title.toLowerCase(Locale.ROOT);
        String slug = NON_ALPHANUMERIC.matcher(normalized).replaceAll("");
        slug = MULTI_SPACE.matcher(slug).replaceAll("-");
        slug = MULTI_HYPHEN.matcher(slug).replaceAll("-");
        if (slug.startsWith("-")) slug = slug.substring(1);
        if (slug.endsWith("-")) slug = slug.substring(0, slug.length() - 1);
        return slug;
    }
}
