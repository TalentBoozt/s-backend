package com.talentboozt.s_backend.domains.edu.learning;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Lesson Completion and Progress Tracker Service.
 * Persists dynamic progress indicators, last views, and quiz markers
 * inside the MongoDB collection "lesson_progress".
 */
@Service
public class LessonProgressService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Saves and logs active progress benchmarks in MongoDB.
     */
    public Map<String, Object> updateLessonProgress(String userId, String lessonId, double progressPercentage, double watchDuration) {
        Map<String, Object> progressMap = new HashMap<>();
        progressMap.put("userId", userId);
        progressMap.put("lessonId", lessonId);
        progressMap.put("progressPercentage", progressPercentage);
        progressMap.put("watchDurationSeconds", watchDuration);
        progressMap.put("isCompleted", progressPercentage >= 100.0);
        progressMap.put("updatedAt", new Date());

        mongoTemplate.save(progressMap, "lesson_progress");
        System.out.println("[Progress Service] Saved progress of " + progressPercentage + "% for user: " + userId);
        return progressMap;
    }
}
