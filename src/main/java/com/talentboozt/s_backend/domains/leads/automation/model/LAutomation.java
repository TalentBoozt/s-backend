package com.talentboozt.s_backend.domains.leads.automation.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "lead_automations")
public class LAutomation {
    @Id
    private String id;
    private String workspaceId;
    private String name;
    private boolean active = true;
    
    // Trigger
    private String triggerType; // AI_SCORE_GT, KEYWORD_MATCH, etc.
    private int triggerScoreThreshold;
    private List<String> triggerKeywords;
    
    // Action
    private String actionType; // GENERATE_REPLY, ADD_TO_CAMPAIGN, etc.
    private String actionTemplateId;
    private boolean requiresHumanApproval = true;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}
