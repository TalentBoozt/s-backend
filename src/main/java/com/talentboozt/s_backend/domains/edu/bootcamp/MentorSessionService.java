package com.talentboozt.s_backend.domains.edu.bootcamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Mentor Booking and Calendar Scheduling Service.
 * Tracks mentor availabilities, schedules meets, and persists bookings
 * in the MongoDB collection "mentor_bookings".
 */
@Service
public class MentorSessionService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Books dynamic mentor consultation schedules and saves coordinates.
     */
    public Map<String, Object> scheduleMentorSession(String userId, String mentorId, Date sessionDate) {
        Map<String, Object> sessionMap = new HashMap<>();
        sessionMap.put("userId", userId);
        sessionMap.put("mentorId", mentorId);
        sessionMap.put("sessionDate", sessionDate);
        sessionMap.put("videoRoomUrl", "https://meet.google.com/talnova-" + UUID.randomUUID().toString().substring(0, 8));
        sessionMap.put("scheduledAt", new Date());

        mongoTemplate.save(sessionMap, "mentor_bookings");
        System.out.println("[Mentor Bookings] Scheduled video session for user: " + userId + " with mentor: " + mentorId);
        return sessionMap;
    }
}
