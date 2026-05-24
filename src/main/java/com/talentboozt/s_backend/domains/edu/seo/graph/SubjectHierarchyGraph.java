package com.talentboozt.s_backend.domains.edu.seo.graph;

import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Educational Subject Hierarchy Ontologies Graph.
 * Resolves sequential hierarchy listings mapping national educational tracks (e.g., Mechanics
 * and Thermodynamics inside Physics) to provide robust crawl context.
 */
@Service
public class SubjectHierarchyGraph {

    /**
     * Resolves high-value curriculum sequence milestones for a target subject track.
     */
    public List<String> getSyllabusHierarchy(String subject) {
        if (subject == null) return List.of("Core Syllabus Concepts", "Past Paper Revisions");
        String normalized = subject.toLowerCase().trim();
        
        if (normalized.contains("physics")) {
            return List.of("Mechanics", "Thermal Physics", "Waves and Oscillations", "Electricity and Magnetism");
        }
        if (normalized.contains("chemistry")) {
            return List.of("General Chemistry", "Inorganic Chemistry", "Physical Chemistry", "Organic Chemistry");
        }
        if (normalized.contains("math")) {
            return List.of("Algebra & Trigonometry", "Calculus & Limits", "Vectors & Statics", "Dynamics & Probability");
        }
        
        return List.of("Core Syllabus Concepts", "Past Paper Revisions");
    }
}
