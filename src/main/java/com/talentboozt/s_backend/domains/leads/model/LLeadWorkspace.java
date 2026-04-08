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
@Document(collection = "leads_workspaces")
public class LLeadWorkspace {
    @Id
    private String id;
    private String name;
    private String ownerId;
    private List<String> memberIds;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
}
