package com.talentboozt.s_backend.domains.leads.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "lead_templates")
public class LTemplate {
    @Id
    private String id;
    private String workspaceId;
    private String name;
    private String content; // supports {{name}}, {{topic}}
    private String platform; // REDDIT, LINKEDIN, ALL
    private String category; // Cold Outreach, Follow Up, Inbound
    private int usageCount = 0;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}
