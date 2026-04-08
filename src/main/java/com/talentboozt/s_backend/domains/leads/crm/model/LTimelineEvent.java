package com.talentboozt.s_backend.domains.leads.crm.model;

import lombok.Data;
import java.time.Instant;

@Data
public class LTimelineEvent {
    private String action; // e.g., "AI Scored", "Status changed", "Note added"
    private String description;
    private Instant timestamp = Instant.now();

    public LTimelineEvent() {}

    public LTimelineEvent(String action, String description) {
        this.action = action;
        this.description = description;
        this.timestamp = Instant.now();
    }
}
