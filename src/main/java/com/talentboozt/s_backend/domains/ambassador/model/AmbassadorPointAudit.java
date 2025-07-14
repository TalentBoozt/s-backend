package com.talentboozt.s_backend.domains.ambassador.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter

@Document(collection = "ambassador_point_audit")
public class AmbassadorPointAudit {
    @Id
    private String id;
    private String ambassadorId;
    private String reason; // DAILY_LOGIN, TASK_COMPLETION, REFERRAL, etc.
    private int points;
    private Instant createdAt;
    private String metadata; // Optional: {"streak":25}
}
