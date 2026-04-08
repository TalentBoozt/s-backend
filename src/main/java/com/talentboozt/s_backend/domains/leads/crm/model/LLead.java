package com.talentboozt.s_backend.domains.leads.crm.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Document(collection = "leads_leads")
public class LLead {
    @Id
    private String id;
    private String workspaceId;
    private String name; // e.g., reddit author username or extracted name
    private String platform;
    private String status = "NEW"; // NEW, CONTACTED, QUALIFIED, CONVERTED
    private List<String> tags = new ArrayList<>();
    private Double score;
    private String sourceSignalId;
    private String notes;
    private List<LTimelineEvent> timeline = new ArrayList<>();
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    public void addTimelineEvent(String action, String description) {
        this.timeline.add(new LTimelineEvent(action, description));
    }
}
