package com.talentboozt.s_backend.domains.jobs.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Document(collection = "job_posts")
public class JobPostModel {
    @Id
    private String id;
    private String companyId;
    private String companyName;
    private String companyLogo;

    private String title;
    private String description;
    private String responsibilities;
    private String requirements;
    private List<String> skills;

    private String location;
    private String remoteType; // REMOTE, HYBRID, ONSITE
    private String type; // e.g., FULL_TIME, PART_TIME, CONTRACT
    private String experienceLevel; // e.g., ENTRY, MID, SENIOR

    private Double minSalary;
    private Double maxSalary;
    private String currency;

    private String status; // e.g., OPEN, CLOSED, DRAFT
    private Instant createdAt;
    private Instant updatedAt;
    private Instant expiryDate;

    /** For AI-powered search & matching */
    private List<Double> embeddings;
    
    /** Match scores for the current user (transient or computed) */
    private int matchScore;

    private int applicationsCount = 0;
    private int viewsCount = 0;

    /** Phase 3: Recruiter Intelligence */
    private List<String> hiringTeam; // User IDs of recruiters assigned to this job
    private String pipelineId; // Reference to custom hiring pipeline
    private String department;
    private String internalNotes;
}
