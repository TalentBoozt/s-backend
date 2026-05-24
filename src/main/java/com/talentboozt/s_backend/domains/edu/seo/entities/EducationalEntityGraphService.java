package com.talentboozt.s_backend.domains.edu.seo.entities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * Educational Entity Graph Service.
 * Structures syllabus keywords and subjects into Schema.org DefinedTerm sets,
 * linking them to Wikitaxonomy nodes to form highly indexable machine-readable profiles.
 */
@Service
public class EducationalEntityGraphService {

    @Autowired
    private SyllabusEntityMapper mapper;

    /**
     * Compiles an educational subject ontology graph matching incoming syllabus details.
     */
    public Map<String, Object> compileEntityGraph(String courseTitle, List<String> syllabusTerms) {
        Map<String, Object> graph = new LinkedHashMap<>();
        graph.put("@context", "https://schema.org");
        graph.put("@type", "DefinedTermSet");
        graph.put("name", courseTitle);
        graph.put("description", "Syllabus mapping graph representing concepts analyzed in " + courseTitle);

        List<Map<String, Object>> definedTerms = new ArrayList<>();
        if (syllabusTerms != null) {
            for (String term : syllabusTerms) {
                Map<String, Object> termObj = new LinkedHashMap<>();
                termObj.put("@type", "DefinedTerm");
                termObj.put("name", term);
                termObj.put("sameAs", "https://www.wikidata.org/wiki/" + mapper.resolveWikidataId(term));
                definedTerms.add(termObj);
            }
        }

        graph.put("hasDefinedTerm", definedTerms);
        return graph;
    }
}
