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
    private String type; // e.g., FULL_TIME, PART_TIME, CONTRACT
    private String experienceLevel; // e.g., ENTRY, MID, SENIOR

    private Double minSalary;
    private Double maxSalary;
    private String currency;

    private String status; // e.g., OPEN, CLOSED, DRAFT
    private Instant createdAt;
    private Instant updatedAt;
    private Instant expiryDate;

    private int applicationsCount = 0;
    private int viewsCount = 0;
}
