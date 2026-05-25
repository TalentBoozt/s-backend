package com.talentboozt.s_backend.domains.edu.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Standardized Skill Node Taxonomy Entity.
 * Maps core career skills, target pathways, and expertise difficulties in the MongoDB collection "edu_skill_nodes".
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_skill_nodes")
public class ESkillNode {

    @Id
    private String id;

    @Indexed(unique = true)
    private String slug;

    private String category; // e.g., AI_AND_AUTOMATION, SOFTWARE_DEVELOPMENT
    private String difficulty; // e.g., BEGINNER, INTERMEDIATE, ADVANCED
    private List<String> careerPaths;
    private List<String> relatedSkills;
}
