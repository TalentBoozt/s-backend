package com.talentboozt.s_backend.domains.edu.seo.analytics;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.Instant;

/**
 * MongoDB document representation for AI Crawler Visit logs.
 */
@Document(collection = "edu_ai_crawler_logs")
public class CrawlVisitLog {

    @Id
    private String id;

    @Indexed
    private String botName;

    @Indexed
    private String requestUri;

    private String userAgent;
    private String ipAddress;
    private String responseTimeMs;
    private int httpStatus;
    private String deliverySummary;

    @Indexed
    private Instant timestamp;

    public CrawlVisitLog() {
        this.timestamp = Instant.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBotName() { return botName; }
    public void setBotName(String botName) { this.botName = botName; }

    public String getRequestUri() { return requestUri; }
    public void setRequestUri(String requestUri) { this.requestUri = requestUri; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getResponseTimeMs() { return responseTimeMs; }
    public void setResponseTimeMs(String responseTimeMs) { this.responseTimeMs = responseTimeMs; }

    public int getHttpStatus() { return httpStatus; }
    public void setHttpStatus(int httpStatus) { this.httpStatus = httpStatus; }

    public String getDeliverySummary() { return deliverySummary; }
    public void setDeliverySummary(String deliverySummary) { this.deliverySummary = deliverySummary; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
