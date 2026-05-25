package com.talentboozt.s_backend.domains.edu.seo.knowledgegraph;

import com.talentboozt.s_backend.domains.edu.model.ECourses;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class CourseRelationshipResolver {

    /**
     * Resolves closely related courses using common categories or tags.
     */
    public List<String> resolveRelatedCourses(ECourses course, List<ECourses> allCourses) {
        List<String> related = new ArrayList<>();
        if (course.getCategories() == null || course.getCategories().length == 0) {
            return related;
        }
        
        String primaryCat = course.getCategories()[0];
        for (ECourses c : allCourses) {
            if (c.getId().equals(course.getId())) continue;
            if (c.getCategories() != null && c.getCategories().length > 0 && primaryCat.equals(c.getCategories()[0])) {
                related.add("https://edu.talnova.io/course/" + c.getSeoSlug());
            }
            if (related.size() >= 3) break;
        }
        return related;
    }
}
