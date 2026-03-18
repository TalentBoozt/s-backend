package com.talentboozt.s_backend.domains.edu.model;

import java.time.Instant;
import java.util.Map;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.talentboozt.s_backend.domains.edu.enums.EAnalyticsEvent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_analytics_events")
@CompoundIndexes({
        @CompoundIndex(name = "user_event_idx", def = "{'userId': 1, 'type': 1}"),
        @CompoundIndex(name = "course_event_idx", def = "{'courseId': 1, 'type': 1}")
})
public class EAnalyticsEvents {
    @Id
    private String id;

    @Indexed
    private EAnalyticsEvent type;

    @Indexed
    private String userId;

    @Indexed
    private String courseId;

    // Extensible payload for context like device maps, lesson IDs, session
    // durations
    private Map<String, Object> metadata;

    @CreatedDate
    @Indexed(name = "expireAtIndex", expireAfter = "31536000s") // Auto expire ancient tracking events (1 year)
    private Instant timestamp;
}
