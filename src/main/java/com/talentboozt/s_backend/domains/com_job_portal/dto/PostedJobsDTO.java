package com.talentboozt.s_backend.domains.com_job_portal.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class PostedJobsDTO {
    @Id
    private String id;
    private String title;
    private String description;
    private String responsibilities;
    private String requirements;
    private String experience;
    private String education;
    private String skills;
    private String qualifications;
    private String totalOpenings;
    private String ageRange;
    private String jobBanner;
    private String employeeType;
    private String locationType;
    private String location;
    private String category;
    private String jobType;
    private String exShortDesc;
    private String eduShortDesc;
    private String salary;
    private String minSalary;
    private String maxSalary;
    private String offers;
    private String datePosted;
    private String expiryDate;
    private String url;
    private String popularityScore;
    private String redirectUrl;
}
