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
}
