package com.talentboozt.s_backend.domains.jobs.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Getter
@Setter
@Document(collection = "applicants")
public class ApplicantModel {
    @Id
    private String id;
    private String jobId;
    private String companyId;

    private String candidateId; // referring to CredentialsModel or a separate Profile
    private String candidateName;
    private String candidateEmail;
    private String candidatePhone;

    private String status; // e.g., PENDING, REVIEWING, INTERVIEWED, HIRED, REJECTED
    private Instant appliedAt;
    private Instant updatedAt;

    private String resumeUrl;
    private String coverLetter;

    private String interviewNotes;
    private String rejectionReason;
}
