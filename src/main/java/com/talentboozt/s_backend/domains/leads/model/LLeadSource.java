package com.talentboozt.s_backend.domains.leads.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@ToString
@Document(collection = "leads_sources")
public class LLeadSource {
    @Id
    private String id;
    private String workspaceId;
    private String platform;
    private String name;
    private Map<String, Object> config;
    private boolean active = true;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
}
