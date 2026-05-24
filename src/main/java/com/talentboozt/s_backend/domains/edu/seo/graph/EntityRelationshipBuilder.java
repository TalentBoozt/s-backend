package com.talentboozt.s_backend.domains.edu.seo.graph;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * Educational Entity Relationship Mapper.
 * Maps context connections connecting teachers, subjects, streams, and exam milestones
 * to feed educational schema metadata graphs.
 */
@Service
public class EntityRelationshipBuilder {

    /**
     * Formulates structural relationship mappings for educational listings.
     */
    public Map<String, Object> buildRelationships(String course, String teacher, String stream, String exam) {
        Map<String, Object> relationshipMap = new HashMap<>();
        
        relationshipMap.put("subject", course);
        relationshipMap.put("teacher", teacher);
        relationshipMap.put("belongsToStream", stream);
        relationshipMap.put("preparesForExam", exam);
        
        return relationshipMap;
    }
}
