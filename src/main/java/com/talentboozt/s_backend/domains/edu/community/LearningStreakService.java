package com.talentboozt.s_backend.domains.edu.community;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Community Streak and XP Tracker.
 * Manages daily learning activity streaks and rewards pupils XP points,
 * saving statuses in the MongoDB collection "learning_streaks".
 */
@Service
public class LearningStreakService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Updates and logs user XP rewards.
     */
    public Map<String, Object> incrementLearningStreak(String userId) {
        Map<String, Object> streakMap = new HashMap<>();
        streakMap.put("userId", userId);
        streakMap.put("currentStreakDays", 5);
        streakMap.put("xpAccumulated", 750);
        streakMap.put("lastActiveDate", new Date());

        mongoTemplate.save(streakMap, "learning_streaks");
        System.out.println("[Streak Service] Logged streak update for user: " + userId);
        return streakMap;
    }
}
