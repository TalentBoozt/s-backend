package com.talentboozt.s_backend.domains.activity.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "activities")
public class JPActivityModel {
    @Id
    private String id;
    private String userId;
    private String organizationId;
    
    private String type; // JOB_PUBLISHED, APPLICATION_SUBMITTED, STAGE_CHANGED, INTERVIEW_SCHEDULED, MESSAGE_SENT
    private String title;
    private String content;
    private String domain; // JOBS, APPLICATIONS, PIPELINE, INTERVIEWS, MESSAGING
    
    private Map<String, Object> metadata;
    
    private Instant timestamp;
}
