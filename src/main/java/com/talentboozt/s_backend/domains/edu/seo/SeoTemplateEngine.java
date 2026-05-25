package com.talentboozt.s_backend.domains.edu.seo;

import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class SeoTemplateEngine {

    /**
     * Resolves meta title patterns dynamically.
     */
    public String renderTitle(String template, Map<String, String> variables) {
        if (template == null) {
            return "Professional Skills, AI Courses & Career Development | Talnova";
        }
        String rendered = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            rendered = rendered.replace("{" + entry.getKey() + "}", entry.getValue() != null ? entry.getValue() : "");
        }
        return cleanString(rendered);
    }

    /**
     * Resolves meta description patterns dynamically.
     */
    public String renderDescription(String template, Map<String, String> variables) {
        if (template == null) {
            return "Empower your career with modern digital skills online at Talnova.";
        }
        String rendered = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            rendered = rendered.replace("{" + entry.getKey() + "}", entry.getValue() != null ? entry.getValue() : "");
        }
        return cleanString(rendered);
    }

    private String cleanString(String str) {
        return str.replaceAll("\\s+", " ").trim();
    }
}
