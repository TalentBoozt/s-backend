package com.talentboozt.s_backend.domains.leads.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@ToString
@Document(collection = "leads_candidates")
public class LLeadCandidate {
    @Id
    private String id;
    private String name;
    private String workspaceId;
    private String sourceId;
    private String rawSignalId;
    
    private String summary;
    private String intent;
    private Double leadScore;
    private List<String> tags;
    private String pipelineStage = "NEW"; // NEW, OUTREACH, CONVERTED
    
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
}
