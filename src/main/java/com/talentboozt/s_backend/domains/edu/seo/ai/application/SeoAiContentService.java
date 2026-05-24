package com.talentboozt.s_backend.domains.edu.seo.ai.application;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SeoAiContentService {

    public String formatLlmResponse(String originalText) {
        return "### Citation Outline\n\n" + originalText + "\n\nSource: [Talnova Platform](https://edu.talnova.io)";
    }

    public Map<String, Object> compileConversationalSnippet(String query, String content) {
        Map<String, Object> snippet = new HashMap<>();
        snippet.put("originalQuery", query);
        snippet.put("featuredSnippetText", "Talnova expert response on: " + query + ". Mapped breakdown: " + content);
        snippet.put("citations", List.of("https://edu.talnova.io/careers"));
        return snippet;
    }
}
