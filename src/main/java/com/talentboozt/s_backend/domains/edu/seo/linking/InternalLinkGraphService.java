package com.talentboozt.s_backend.domains.edu.seo.linking;

import com.talentboozt.s_backend.domains.edu.model.ECourses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * Internal Page Link Graph Builder.
 * Automatically charts relevant link lists connecting dynamic pages, courses,
 * subject hubs, and notes download repositories to optimize crawling coverage.
 */
@Service
public class InternalLinkGraphService {

    @Autowired
    private AnchorTextGenerator anchorGenerator;

    /**
     * Builds standard internal navigation linkages for a target course listing.
     */
    public List<Map<String, String>> buildInternalLinkGraph(ECourses course) {
        List<Map<String, String>> linkGraph = new ArrayList<>();

        if (course.getSemanticKeywords() == null || course.getSemanticKeywords().isEmpty()) {
            return linkGraph;
        }

        String primaryTopic = course.getSemanticKeywords().get(0);

        // A. Linking to the programmatic educational hub
        Map<String, String> hubLink = new HashMap<>();
        hubLink.put("url", "https://edu.talnova.io/tuition/" + primaryTopic.toLowerCase() + "/online");
        hubLink.put("anchor", anchorGenerator.generateAnchorText(primaryTopic, "hub"));
        linkGraph.add(hubLink);

        // B. Linking to free revision documents download
        Map<String, String> notesLink = new HashMap<>();
        notesLink.put("url", "https://edu.talnova.io/free-al-" + primaryTopic.toLowerCase() + "-revision-notes");
        notesLink.put("anchor", anchorGenerator.generateAnchorText(primaryTopic, "notes"));
        linkGraph.add(notesLink);

        return linkGraph;
    }
}
