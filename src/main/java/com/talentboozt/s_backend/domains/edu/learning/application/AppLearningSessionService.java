package com.talentboozt.s_backend.domains.edu.learning.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AppLearningSessionService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public Map<String, Object> logLearningSession(String userId, double sessionMinutes, double engagementScore) {
        Map<String, Object> sessionMap = new HashMap<>();
        sessionMap.put("userId", userId);
        sessionMap.put("sessionMinutes", sessionMinutes);
        sessionMap.put("engagementScore", engagementScore);
        sessionMap.put("loggedAt", new Date());

        mongoTemplate.save(sessionMap, "learning_sessions");
        System.out.println("[Clean Session] Logged learning session of " + sessionMinutes + " mins for: " + userId);
        return sessionMap;
    }
}
