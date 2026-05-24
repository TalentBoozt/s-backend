package com.talentboozt.s_backend.domains.edu.career;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * Career Pathway Compiler Service.
 * Formulates chronological roadmaps, target salary scopes, course sequences,
 * and checkpoint matrices in the MongoDB collection "career_paths".
 */
@Service
public class CareerPathService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Compiles detailed career pathways and saves entries to MongoDB.
     */
    public Map<String, Object> compileCareerPathway(String title, String slug) {
        Map<String, Object> pathway = new HashMap<>();
        pathway.put("title", title);
        pathway.put("slug", slug);
        pathway.put("estimatedDuration", "6 Months");
        pathway.put("salaryRange", "$45,000 - $85,000 / Year");
        
        List<String> courses = List.of(
            "Intro to HTML/CSS", 
            "Modern JavaScript Essentials", 
            "React 19 Frontend Development"
        );
        pathway.put("orderedCourses", courses);

        List<String> checkpoints = List.of(
            "Semantic Markup Quiz", 
            "Single Page App Deployment", 
            "Portfolio Presentation Review"
        );
        pathway.put("skillCheckpoints", checkpoints);
        pathway.put("createdDate", new Date());

        mongoTemplate.save(pathway, "career_paths");
        System.out.println("[Career Path Engine] Logged career path: " + slug);
        return pathway;
    }
}
