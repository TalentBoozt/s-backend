package com.talentboozt.s_backend.domains.edu.seo.knowledgegraph;

import com.talentboozt.s_backend.domains.edu.model.ECourses;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TopicClusterResolver {

    /**
     * Clusters courses dynamically into semantic hubs based on primary categories.
     */
    public Map<String, List<ECourses>> buildTopicClusters(List<ECourses> allCourses) {
        Map<String, List<ECourses>> clusters = new HashMap<>();
        for (ECourses c : allCourses) {
            if (c.getCategories() != null && c.getCategories().length > 0) {
                String primaryCat = c.getCategories()[0];
                clusters.computeIfAbsent(primaryCat, k -> new ArrayList<>()).add(c);
            }
        }
        return clusters;
    }
}
