package com.talentboozt.s_backend.domains.finance_planning.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "fin_workspaces")
public class FinWorkspace {
    @Id
    private String id;
    private String name;
    private String slug;
    private String ownerId;
    private List<String> memberIds;
    private String subscriptionType; // "FREE", "PRO", "ENTERPRISE"
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
}
