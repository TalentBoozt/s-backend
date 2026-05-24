package com.talentboozt.s_backend.domains.edu.learning.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AppLessonProgressService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public Map<String, Object> updateLessonProgress(String userId, String lessonId, double progressPercentage, double watchDuration) {
        Map<String, Object> progressMap = new HashMap<>();
        progressMap.put("userId", userId);
        progressMap.put("lessonId", lessonId);
        progressMap.put("progressPercentage", progressPercentage);
        progressMap.put("watchDurationSeconds", watchDuration);
        progressMap.put("isCompleted", progressPercentage >= 100.0);
        progressMap.put("updatedAt", new Date());

        mongoTemplate.save(progressMap, "lesson_progress");
        System.out.println("[Clean Progress] Saved progress of " + progressPercentage + "% for: " + userId);
        return progressMap;
    }
}
