package com.talentboozt.s_backend.domains.leads.automation.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Document(collection = "leads_automations")
public class LLeadAutomation {
    @Id
    private String id;
    private String workspaceId;
    private String name;
    private boolean active = true;

    // e.g., AI_SCORE_GT, KEYWORD_MATCH, STATUS_CHANGE
    private String triggerType;
    private Double triggerScoreThreshold;
    private List<String> triggerKeywords;
    private String triggerStatus;

    // e.g., NOTIFY_USER, GENERATE_REPLY, MOVE_TO_PIPELINE, WEBHOOK
    private String actionType;
    private String actionWebhookUrl;
    private boolean requiresHumanApproval = true; // Safety layer

    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
}
