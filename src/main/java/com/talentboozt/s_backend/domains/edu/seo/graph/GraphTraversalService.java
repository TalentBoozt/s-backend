package com.talentboozt.s_backend.domains.edu.seo.graph;

import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Educational Knowledge Graph Traversal Engine.
 * Resolves curriculum prerequisites sequences (e.g., Mechanics to Forces,
 * Newton's Laws, and Linear Momentum) for targeted search engine visibility.
 */
@Service
public class GraphTraversalService {

    /**
     * Maps prerequisite learning milestones for a target syllabus area.
     */
    public List<String> resolvePrerequisiteChain(String syllabusTopic) {
        if (syllabusTopic == null) return List.of("Basic Concept Foundations");
        String normalized = syllabusTopic.toLowerCase().trim();
        
        if (normalized.contains("momentum") || normalized.contains("newton") || normalized.contains("force")) {
            return List.of("Mechanics Core Principles", "Vector Forces Analysis", "Newton's Kinetic Laws", "Linear Momentum");
        }
        if (normalized.contains("organic")) {
            return List.of("General Chemistry Foundations", "Hydrocarbons Structure", "Functional Group Alcohols", "Organic Synthesis Reagents");
        }
        
        return List.of("Basic Concept Foundations", syllabusTopic);
    }
}
