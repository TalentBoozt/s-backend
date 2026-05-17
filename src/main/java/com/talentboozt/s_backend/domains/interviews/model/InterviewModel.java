package com.talentboozt.s_backend.domains.interviews.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "interviews")
public class InterviewModel {
    @Id
    private String id;
    private String applicationId;
    private String jobId;
    private String candidateId;
    private List<String> interviewerIds; // User IDs of recruiters/interviewers
    
    private String type; // PHONE, VIDEO, TECHNICAL, PANEL, FINAL
    private String status; // SCHEDULED, CONFIRMED, COMPLETED, CANCELLED
    
    private Instant startTime;
    private Instant endTime;
    private String location; // Physical address or meeting link
    
    private List<InterviewFeedback> feedback;
    
    private Instant createdAt;
    private Instant updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InterviewFeedback {
        private String interviewerId;
        private int rating; // 1-5
        private String comments;
        private String decision; // HIRE, NO_HIRE, STRONG_HIRE, WEAK_HIRE
        private Instant submittedAt;
    }
}
