package com.talentboozt.s_backend.domains.edu.seo.graph;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * National Curriculum Ontology Mapper.
 * Catalogues dynamic study streams, target grade structures, and Sri Lankan educational
 * exam specifications to enrich Schema.org and AI engine definitions.
 */
@Service
public class CurriculumOntologyService {

    /**
     * Maps subject areas to National Institute of Education (NIE) curriculum properties.
     */
    public Map<String, Object> resolveOntologyMetadata(String subject) {
        Map<String, Object> ontologyMap = new HashMap<>();
        if (subject == null) return ontologyMap;
        
        String normalized = subject.toLowerCase().trim();

        if (normalized.contains("physics")) {
            ontologyMap.put("subjectCode", "GCE-AL-PHYSICS");
            ontologyMap.put("stream", "Physical Science");
            ontologyMap.put("grade", "Grade 12-13");
            ontologyMap.put("examination", "G.C.E. Advanced Level");
        } else if (normalized.contains("math")) {
            ontologyMap.put("subjectCode", "GCE-AL-COMBINED-MATHS");
            ontologyMap.put("stream", "Physical Science");
            ontologyMap.put("grade", "Grade 12-13");
            ontologyMap.put("examination", "G.C.E. Advanced Level");
        } else if (normalized.contains("chemistry")) {
            ontologyMap.put("subjectCode", "GCE-AL-CHEMISTRY");
            ontologyMap.put("stream", "Physical / Biological Science");
            ontologyMap.put("grade", "Grade 12-13");
            ontologyMap.put("examination", "G.C.E. Advanced Level");
        } else {
            ontologyMap.put("subjectCode", "GCE-AL-GENERIC");
            ontologyMap.put("stream", "General Academic");
            ontologyMap.put("grade", "Grade 12-13");
            ontologyMap.put("examination", "G.C.E. Advanced Level");
        }

        return ontologyMap;
    }
}
