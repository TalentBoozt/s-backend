package com.talentboozt.s_backend.domains.finance_planning.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.Map;

@Data
@Document(collection = "fin_sales_plans")
public class FinSalesPlan implements VersionedEntity {
    @Id
    private String id;
    private String organizationId;
    private String projectId;
    private String month; // ISO format
    private Map<String, Integer> userCounts; // free, pro, premium
    private Double growthRate;
    @Version
    private Integer version;
    private Instant createdAt;
}