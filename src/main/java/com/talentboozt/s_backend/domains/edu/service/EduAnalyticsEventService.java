package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.enums.EAnalyticsEvent;
import com.talentboozt.s_backend.domains.edu.model.EAnalyticsEvents;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EAnalyticsEventsRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class EduAnalyticsEventService {

    private final EAnalyticsEventsRepository eventsRepository;

    public EduAnalyticsEventService(EAnalyticsEventsRepository eventsRepository) {
        this.eventsRepository = eventsRepository;
    }

    // Fire-and-forget logic so it never blocks HTTP request pipelines
    @Async
    public void recordEvent(EAnalyticsEvent type, String userId, String courseId, Map<String, Object> metadata) {
        EAnalyticsEvents event = EAnalyticsEvents.builder()
                .type(type)
                .userId(userId)
                .courseId(courseId)
                .metadata(metadata)
                .timestamp(Instant.now())
                .build();
                
        eventsRepository.save(event);
    }

    /**
     * Unified tracking method matching requested objective.
     */
    @Async
    public void trackEvent(String userId, EAnalyticsEvent type, Map<String, Object> metadata) {
        // userId can be null for public views
        recordEvent(type, userId, (String) metadata.get("courseId"), metadata);
    }

    public java.util.List<EAnalyticsEvents> getEventsByUser(String userId) {
        return eventsRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    /**
     * Basic metric aggregation placeholder for high-level summaries.
     */
    public Map<String, Long> aggregateMetrics() {
        java.util.Map<String, Long> metrics = new java.util.HashMap<>();
        for (EAnalyticsEvent type : EAnalyticsEvent.values()) {
            metrics.put(type.name(), eventsRepository.countByType(type));
        }
        return metrics;
    }
}
