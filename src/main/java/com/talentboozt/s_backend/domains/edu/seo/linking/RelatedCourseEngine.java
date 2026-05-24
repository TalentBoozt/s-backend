package com.talentboozt.s_backend.domains.edu.seo.linking;

import com.talentboozt.s_backend.domains.edu.seo.model.CourseDocument;
import com.talentboozt.s_backend.domains.edu.seo.repository.CourseSeoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * Related Course Recommendation Engine.
 * Dynamically resolves list candidates that share highly matched educational contexts,
 * feeding localized links maps directly.
 */
@Service
public class RelatedCourseEngine {

    @Autowired
    private CourseSeoRepository courseRepository;

    @Autowired
    private SemanticLinkScorer scorer;

    /**
     * Finds related courses for a target subject matching score thresholds.
     */
    public List<CourseDocument> resolveRelatedCourses(CourseDocument currentCourse, int limit) {
        List<CourseDocument> allCourses = courseRepository.findAllIndexableProjections();
        List<CourseDocument> results = new ArrayList<>();

        for (CourseDocument course : allCourses) {
            if (course.getId().equals(currentCourse.getId())) continue;

            double score = scorer.calculateRelevanceScore(
                currentCourse.getSemanticKeywords(), 
                course.getSemanticKeywords()
            );
            
            if (score > 0.1) {
                results.add(course);
            }
            if (results.size() >= limit) break;
        }

        return results;
    }
}
