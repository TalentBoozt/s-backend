package com.talentboozt.s_backend.domains.edu.community;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Community Discussion Forums Service.
 * Records community discussions, student showcases, and Q&As inside
 * the MongoDB collection "community_threads".
 */
@Service
public class CommunityDiscussionService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Initializes and logs discussion topics.
     */
    public Map<String, Object> createCommunityDiscussion(String title, String category, String authorName) {
        Map<String, Object> threadMap = new HashMap<>();
        threadMap.put("title", title);
        threadMap.put("category", category);
        threadMap.put("author", authorName);
        threadMap.put("createdDate", new Date());

        mongoTemplate.save(threadMap, "community_threads");
        System.out.println("[Community Discussion] Logged discussion thread for: " + title);
        return threadMap;
    }
}
