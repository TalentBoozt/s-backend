package com.talentboozt.s_backend.Model.SYS_TRACKING;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Data
@Document(collection = "events")
public class TrackingEvent {
    @Id
    private String id;
    private String trackingId; // SaaS app identifier
    private String eventType;  // e.g., page_view, click
    private String url;
    private String referrer;
    private String sessionId;
    private String userId;
    private String elementId;
    private String elementText;
    private String elementType;
    private String screenResolution;
    private String browser;
    private String language;
    private String ip;
    private String country;
    private String region;
    private String city;
    private Instant timestamp;
}
