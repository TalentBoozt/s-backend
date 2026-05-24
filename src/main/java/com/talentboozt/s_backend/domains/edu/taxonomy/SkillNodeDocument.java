package com.talentboozt.s_backend.domains.edu.taxonomy;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

/**
 * Skill Node Taxonomy Entity.
 * Maps core career skills, target pathways, and expertise difficulties
 * in the MongoDB collection "skill_nodes".
 */
@Document(collection = "skill_nodes")
public class SkillNodeDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String slug;

    private String category; // e.g., AI_AND_AUTOMATION, SOFTWARE_DEVELOPMENT
    private String difficulty; // e.g., BEGINNER, INTERMEDIATE, ADVANCED
    private List<String> careerPaths;
    private List<String> relatedSkills;

    public SkillNodeDocument() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public List<String> getCareerPaths() { return careerPaths; }
    public void setCareerPaths(List<String> careerPaths) { this.careerPaths = careerPaths; }

    public List<String> getRelatedSkills() { return relatedSkills; }
    public void setRelatedSkills(List<String> relatedSkills) { this.relatedSkills = relatedSkills; }
}
