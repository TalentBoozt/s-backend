package com.talentboozt.s_backend.domains.edu.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_payout_schedules")
public class EPayoutSchedule {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String creatorId;
    
    /** e.g. WEEKLY, MONTHLY, MANUAL */
    private String frequency;
    
    /** e.g. MONDAY for weekly, or 1 for 1st of month */
    private String dayTarget;
    
    private Boolean active;
    
    private Instant lastProcessedAt;
    private Instant nextScheduledAt;
    
    @CreatedDate
    private Instant createdAt;
}
