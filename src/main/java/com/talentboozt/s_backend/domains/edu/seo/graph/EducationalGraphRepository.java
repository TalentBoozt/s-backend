package com.talentboozt.s_backend.domains.edu.seo.graph;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import java.util.*;

/**
 * Educational Knowledge Graph Repository.
 * Saves knowledge node structures (subjects, teacher nodes, syllabus segments)
 * into independent collections inside MongoDB.
 */
@Repository
public class EducationalGraphRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Preserves subject node definitions in MongoDB.
     */
    public void saveSubjectNode(String nodeId, String title, String wikidataId) {
        Map<String, Object> subjectNode = new HashMap<>();
        subjectNode.put("id", nodeId);
        subjectNode.put("label", title);
        subjectNode.put("wikidataId", wikidataId);
        subjectNode.put("updatedAt", new Date());
        
        mongoTemplate.save(subjectNode, "subject_nodes");
    }

    /**
     * Preserves topic syllabus nodes and prerequisite requirements.
     */
    public void saveTopicNode(String nodeId, String subjectNodeId, String title, List<String> prerequisitesList) {
        Map<String, Object> topicNode = new HashMap<>();
        topicNode.put("id", nodeId);
        topicNode.put("subjectId", subjectNodeId);
        topicNode.put("label", title);
        topicNode.put("prerequisites", prerequisitesList);
        topicNode.put("updatedAt", new Date());

        mongoTemplate.save(topicNode, "topic_nodes");
    }
}
