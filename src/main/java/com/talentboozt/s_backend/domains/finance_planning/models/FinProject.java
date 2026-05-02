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
@Document(collection = "fin_projects")
public class FinProject {
    @Id
    private String id;
    private String organizationId;
    private String name;
    private String description;
    private String type; // e.g., "SAAS", "ECOMMERCE", "SERVICE"
    private String status; // "DRAFT", "REVIEW", "APPROVED"
    private String ownerId;
    private List<String> teamMemberIds;
    private Instant createdAt;
    private Instant updatedAt;
}
