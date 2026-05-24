package com.talentboozt.s_backend.domains.edu.seo.entities;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * Educational Syllabus Wikidata Entity Mapper.
 * Maps dynamic course syllabus tracks to verified Wikidata global entities,
 * elevating GEO/AEO classification indices for search engine crawlers.
 */
@Service
public class SyllabusEntityMapper {

    private final Map<String, String> entityWikidataMap = new HashMap<>();

    public SyllabusEntityMapper() {
        // Enforce localized Sri Lankan G.C.E. Advanced Level Wikidata subject codes
        entityWikidataMap.put("physics", "Q413");
        entityWikidataMap.put("chemistry", "Q413");
        entityWikidataMap.put("combined-mathematics", "Q1351230");
        entityWikidataMap.put("calculus", "Q2392");
        entityWikidataMap.put("organic-chemistry", "Q15754");
        entityWikidataMap.put("mechanics", "Q41217");
        entityWikidataMap.put("rotational-dynamics", "Q1087192");
    }

    /**
     * Resolves global Wikidata identifier matching raw syllabus terminology.
     */
    public String resolveWikidataId(String term) {
        if (term == null) return "entity-wikidata-placeholder";
        return entityWikidataMap.getOrDefault(
            term.toLowerCase().trim().replace(" ", "-"), 
            "entity-wikidata-placeholder"
        );
    }
}
