package com.talentboozt.s_backend.domains.applications.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Getter
@Setter
@Document(collection = "applications")
public class ApplicationModel {
    @Id
    private String id;
    private String jobId;
    private String companyId;
    private String employeeId;

    private String resumeId;
    private String resumeVersionId;
    private String coverLetter;

    private String status; // APPLIED, SCREENING, SHORTLISTED, INTERVIEW, OFFER, REJECTED, WITHDRAWN
    private int matchScore; // AI computed match score at time of application

    private Instant appliedAt;
    private Instant updatedAt;

    private String interviewNotes;
    private String rejectionReason;
}
