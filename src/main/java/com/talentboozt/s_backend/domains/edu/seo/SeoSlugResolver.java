package com.talentboozt.s_backend.domains.edu.seo;

import org.springframework.stereotype.Component;
import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

@Component
public class SeoSlugResolver {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    /**
     * Standardizes a raw text input to a canonical, SEO-safe slug.
     */
    public String resolveToSafeSlug(String input) {
        if (input == null || input.isBlank()) {
            return "general-hub";
        }
        
        String nowhitespace = WHITESPACE.matcher(input.trim()).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        
        slug = slug.toLowerCase(Locale.ENGLISH);
        
        // Remove duplicate hyphens
        while (slug.contains("--")) {
            slug = slug.replace("--", "-");
        }
        
        // Trim leading and trailing hyphens
        if (slug.startsWith("-")) {
            slug = slug.substring(1);
        }
        if (slug.endsWith("-")) {
            slug = slug.substring(0, slug.length() - 1);
        }
        
        return slug.isEmpty() ? "general-hub" : slug;
    }
}
