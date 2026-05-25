package com.talentboozt.s_backend.domains.edu.taxonomy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import com.talentboozt.s_backend.domains.edu.model.ESkillNode;
import java.util.List;

/**
 * Learning Categories and Taxonomy Management Service.
 * Presets top-level learning streams and populates base dynamic skill nodes in MongoDB.
 */
@Service
public class LearningCategoryGraphService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Lists all supported career acceleration categories.
     */
    public List<String> getTopLevelCategories() {
        return List.of(
            "CAREER_READINESS",
            "AI_AND_AUTOMATION",
            "FREELANCING",
            "SOFTWARE_DEVELOPMENT",
            "DIGITAL_MARKETING",
            "CREATIVE_SKILLS",
            "BUSINESS",
            "ACADEMIC_SUPPORT"
        );
    }

    /**
     * Dynamic initialization task compiling skill definitions.
     */
    public void initializeDefaultSkillTaxonomies() {
        System.out.println("[Taxonomy Service] Initializing top-level skills taxonomies...");
        
        ESkillNode node = new ESkillNode();
        node.setSlug("prompt-engineering");
        node.setCategory("AI_AND_AUTOMATION");
        node.setDifficulty("BEGINNER");
        node.setCareerPaths(List.of("ai-content-creator", "prompt-engineer"));
        node.setRelatedSkills(List.of("chatgpt", "ai-automation"));

        mongoTemplate.save(node);
        System.out.println("[Taxonomy Service] Preset taxonomy initialized for: prompt-engineering");
    }
}
