package com.talentboozt.s_backend.domains.leads.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Document(collection = "lead_background_tasks")
public class LTask {
    @Id
    private String id;
    private String workspaceId;
    private String userId;
    
    private String type; // AI_TEMPLATE_GEN, LEAD_SCRAPE, CAMPAIGN_DISPATCH
    private String status = "PENDING"; // PENDING, PROCESSING, COMPLETED, FAILED
    private int progress = 0; // 0-100
    
    private String resultUrl; // Link to the resource once done (e.g. signal ID or template ID)
    private String errorMessage;
    
    private Map<String, Object> metadata;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}
