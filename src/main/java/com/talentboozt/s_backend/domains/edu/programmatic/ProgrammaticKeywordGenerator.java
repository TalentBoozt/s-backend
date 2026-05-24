package com.talentboozt.s_backend.domains.edu.programmatic;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * Programmatic Educational Keyword & Slug Generator.
 * Merges primary subject areas, localized target districts, and media levels to yield
 * high-value long-tail search targets.
 */
@Service
public class ProgrammaticKeywordGenerator {

    private final List<String> subjects = List.of("physics", "combined-maths", "chemistry", "biology");
    private final List<String> locations = List.of("colombo", "kandy", "gampaha", "online");
    private final List<String> mediums = List.of("sinhala", "english", "tamil");

    /**
     * Programmatically builds tuition search slug patterns.
     */
    public List<String> generateTargetSlugs() {
        List<String> slugs = new ArrayList<>();
        
        for (String subject : subjects) {
            // A. Location Tuitions e.g. /tuition/physics/colombo
            for (String loc : locations) {
                slugs.add("tuition/" + subject + "/" + loc);
            }
            
            // B. Medium Tuitions e.g. /tuition/physics/sinhala
            for (String med : mediums) {
                slugs.add("tuition/" + subject + "/" + med);
            }
            
            // C. Long-tail target templates
            slugs.add("best-al-" + subject + "-teachers-sri-lanka");
            slugs.add("free-al-" + subject + "-revision-notes");
        }
        
        return slugs;
    }
}
