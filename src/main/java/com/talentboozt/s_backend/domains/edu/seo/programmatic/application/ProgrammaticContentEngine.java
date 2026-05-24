package com.talentboozt.s_backend.domains.edu.seo.programmatic.application;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class ProgrammaticContentEngine {

    public Map<String, Object> compileProgrammaticPage(String focusRole) {
        Map<String, Object> page = new HashMap<>();
        String slug = "/how-to-become/" + focusRole.toLowerCase().trim().replace(" ", "-");
        
        page.put("slug", slug);
        page.put("focusKeyword", "how to become a " + focusRole);
        page.put("metaTitle", "How to Become a " + focusRole + " | Career Roadmap");
        page.put("metaDescription", "Explore structured study roadmaps, recommended learning modules, and salary milestones to become a " + focusRole + ".");
        page.put("structuredDataJson", "{\"@context\":\"https://schema.org\",\"@type\":\"ItemPage\",\"name\":\"Become a " + focusRole + "\"}");
        
        return page;
    }
}
