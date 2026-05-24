package com.talentboozt.s_backend.domains.edu.learning;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Active Learning Session Management Service.
 * Persists dynamic pupil sessions logs, duration metrics, and engagement valuations
 * inside the MongoDB collection "learning_sessions".
 */
@Service
public class LearningSessionService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Saves and logs active session metrics.
     */
    public Map<String, Object> logLearningSession(String userId, double sessionMinutes, double engagementScore) {
        Map<String, Object> sessionMap = new HashMap<>();
        sessionMap.put("userId", userId);
        sessionMap.put("sessionMinutes", sessionMinutes);
        sessionMap.put("engagementScore", engagementScore);
        sessionMap.put("loggedAt", new Date());

        mongoTemplate.save(sessionMap, "learning_sessions");
        System.out.println("[Session Service] Logged active study session for user: " + userId);
        return sessionMap;
    }
}
