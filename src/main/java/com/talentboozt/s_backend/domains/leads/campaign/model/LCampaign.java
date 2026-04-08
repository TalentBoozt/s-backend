package com.talentboozt.s_backend.domains.leads.campaign.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@Document(collection = "leads_campaigns")
public class LCampaign {
    @Id
    private String id;
    private String workspaceId;
    private String name;
    private String platform; // REDDIT, LINKEDIN, etc.
    private String status = "ACTIVE"; // ACTIVE, PAUSED, COMPLETED
    private String template;
    private String sourceId; // ID of the LLeadSource being targeted
    
    private Map<String, Integer> metrics = new HashMap<>() {{
        put("sent", 0);
        put("replied", 0);
        put("converted", 0);
    }};

    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
}
