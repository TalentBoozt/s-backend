package com.talentboozt.s_backend.domains.edu.seo.graph;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Technical Educational Knowledge Graph Service.
 * Compiles dynamic subject hierarchies, entity associations, and exam credential variables
 * into Schema.org representations.
 */
@Service
public class KnowledgeGraphService {

    @Autowired
    private SubjectHierarchyGraph hierarchyGraph;

    @Autowired
    private CurriculumOntologyService ontologyService;

    @Autowired
    private EntityRelationshipBuilder relationshipBuilder;

    /**
     * Compiles unified educational graphs mapping subjects to credentials.
     */
    public Map<String, Object> compileEducationalGraph(String subject, String teacherName) {
        Map<String, Object> knowledgeGraph = new HashMap<>();
        
        Map<String, Object> ontology = ontologyService.resolveOntologyMetadata(subject);
        String stream = (String) ontology.getOrDefault("stream", "Physical Science");
        String exam = (String) ontology.getOrDefault("examination", "G.C.E. Advanced Level");
        
        // 1. Map dynamic subject relationship indexes
        Map<String, Object> relationships = relationshipBuilder.buildRelationships(subject, teacherName, stream, exam);
        knowledgeGraph.put("relations", relationships);

        // 2. Structure DefinedTermSets from syllabus hierarchy
        List<String> syllabus = hierarchyGraph.getSyllabusHierarchy(subject);
        Map<String, Object> definedTermSet = new HashMap<>();
        definedTermSet.put("@type", "DefinedTermSet");
        definedTermSet.put("name", subject + " G.C.E. A/L Syllabus Tracks");
        
        List<Map<String, String>> terms = new ArrayList<>();
        for (String milestone : syllabus) {
            Map<String, String> term = new HashMap<>();
            term.put("@type", "DefinedTerm");
            term.put("name", milestone);
            term.put("termCode", milestone.toLowerCase().replace(" ", "-"));
            terms.add(term);
        }
        definedTermSet.put("hasDefinedTerm", terms);
        knowledgeGraph.put("syllabusSect", definedTermSet);

        // 3. Model EducationalOccupationalCredential schemas
        Map<String, Object> credentialSchema = new HashMap<>();
        credentialSchema.put("@type", "EducationalOccupationalCredential");
        credentialSchema.put("name", exam);
        credentialSchema.put("credentialCategory", "National High School Advanced Level Certificate");
        knowledgeGraph.put("credential", credentialSchema);

        return knowledgeGraph;
    }
}
