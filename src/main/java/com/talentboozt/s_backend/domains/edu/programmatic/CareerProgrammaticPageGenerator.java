package com.talentboozt.s_backend.domains.edu.programmatic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * Technical Career Programmatic Page Generator.
 * Generates scale landing directories (e.g. /how-to-become/frontend-developer,
 * /skills/prompt-engineering) in the MongoDB collection "programmatic_pages"
 * to capture high-volume non-academic search intents.
 */
@Service
public class CareerProgrammaticPageGenerator {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Spawns non-academic career and skill resource directories.
     */
    public Map<String, Object> generateCareerProgrammaticPage(String categoryType, String pathwayName) {
        String targetSlug = "/" + categoryType + "/" + pathwayName.toLowerCase().replace(" ", "-");
        
        Map<String, Object> programmaticPage = new HashMap<>();
        programmaticPage.put("slug", targetSlug);
        programmaticPage.put("title", "How to Become an Expert " + pathwayName + " - Sri Lanka Career Accelerator Guide");
        programmaticPage.put("description", "Start your roadmap to become a highly paid " + pathwayName + 
                                            ". Explore ordered courses, required skill checkpoints, and verified portfolio standards.");
        programmaticPage.put("categoryType", categoryType);
        programmaticPage.put("generatedAt", new Date());

        mongoTemplate.save(programmaticPage, "programmatic_pages");
        System.out.println("[Career Programmatic SEO] Generated page at slug: " + targetSlug);
        return programmaticPage;
    }
}
