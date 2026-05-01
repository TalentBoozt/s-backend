package com.talentboozt.s_backend.domains.finance_planning.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data
@Document(collection = "assumptions_collection")
public class FinAssumption implements VersionedEntity {
    @Id
    private String id;
    private String organizationId;
    private String projectId;
    private String key;
    private String value;
    private String unit;
    private String category;
    @Version
    private Integer version;
    private Instant createdAt;
}