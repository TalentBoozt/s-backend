package com.talentboozt.s_backend.domains.edu.seo.indexing.application;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InternalLinkingService {

    public List<Map<String, Object>> mapInternalLinks(String targetRole) {
        List<Map<String, Object>> suggestions = new ArrayList<>();

        Map<String, Object> link1 = new HashMap<>();
        link1.put("anchorText", "remote " + targetRole + " roadmaps");
        link1.put("url", "https://edu.talnova.io/how-to-become/" + targetRole.toLowerCase().replace(" ", "-"));
        link1.put("linkScore", 0.96);
        suggestions.add(link1);

        return suggestions;
    }
}
