package com.talentboozt.s_backend.domains.automation.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "automation_rules")
public class AutomationRule {
    @Id
    private String id;
    private String organizationId;
    private String name;
    private String description;
    
    private String triggerType; // CANDIDATE_APPLIED, STAGE_CHANGED, SCORE_ABOVE_THRESHOLD
    private Map<String, Object> conditions;
    
    private List<Action> actions;
    
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Action {
        private String type; // MOVE_STAGE, SEND_MESSAGE, NOTIFY_RECRUITER, TRIGGER_AI_TASK
        private Map<String, Object> parameters;
    }
}
