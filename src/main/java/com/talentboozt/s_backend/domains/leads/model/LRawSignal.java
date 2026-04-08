package com.talentboozt.s_backend.domains.leads.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@Document(collection = "leads_raw_signals")
public class LRawSignal {
    @Id
    private String id;
    private String sourceId;
    private String workspaceId;
    private String platformId;
    private String content;
    private String author;
    private String url;
    private Map<String, Object> metadata;
    private String status = "NEW"; // NEW, PROCESSED, DISCARDED
    private String intent; // LEARNING, BUYING, PROBLEM_SOLVING, NOISE
    private Double score;
    private List<String> tags;
    private Instant capturedAt = Instant.now();
}
