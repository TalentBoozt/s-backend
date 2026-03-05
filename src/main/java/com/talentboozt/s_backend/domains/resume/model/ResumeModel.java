package com.talentboozt.s_backend.domains.resume.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

/**
 * Root document for a resume in the Talnova Resume Builder.
 * Stored in MongoDB collection: "resumes"
 *
 * employeeId links back to the SSO user (CredentialsModel.employeeId).
 */
@Document(collection = "resumes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumeModel {

    @Id
    private String id;

    /** SSO user identifier — ties resume to authenticated user across all platforms */
    @Indexed
    private String employeeId;

    /** Human-readable title, e.g. "My Software Engineer Resume" */
    private String title;

    /** Active template key: modern | elegant | compact */
    private String templateId;

    private PersonalInfo personalInfo;
    private List<WorkExperience> workExperience;
    private List<Education> education;
    private List<Skill> skills;
    private List<Project> projects;
    private List<Certificate> certificates;
    private List<CustomSection> customSections;
    private List<String> sectionOrder;

    private ResumeSettings settings;

    /** Scores computed server-side */
    private int completionScore;
    private int atsScore;

    /** AI usage counter — resets never, per-resume limit of 5 calls */
    private int aiUsageCount;

    /** Soft-delete flag */
    private boolean deleted;

    /** SSO platform this resume was created on */
    private String platform;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    // ─── Nested Types ────────────────────────────────────────────────────────

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class PersonalInfo {
        private String fullName;
        private String title;
        private String phone;
        private String email;
        private String location;
        private String linkedin;
        private String portfolio;
        private String summary;
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class WorkExperience {
        private String id;
        private String jobTitle;
        private String company;
        private String location;
        private String startDate;
        private String endDate;
        private boolean current;
        private List<String> responsibilities;
        private List<String> achievements;
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class Education {
        private String id;
        private String degree;
        private String institution;
        private String location;
        private String graduationDate;
        private String gpa;
        private String description;
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class Skill {
        private String id;
        private String name;
        private String level; // beginner | intermediate | advanced | expert
        private String category;
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class Project {
        private String id;
        private String title;
        private String description;
        private List<String> technologies;
        private String githubLink;
        private String liveLink;
        private List<String> achievements;
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class Certificate {
        private String id;
        private String name;
        private String organization;
        private String issueDate;
        private String credentialLink;
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class CustomSection {
        private String id;
        private String title;
        private List<CustomSectionItem> items;
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class CustomSectionItem {
        private String id;
        private String content;
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class ResumeSettings {
        private String primaryColor;
        private String fontFamily;
        private String fontSize;    // small | medium | large
        private String spacing;     // compact | normal | relaxed
        private boolean showPhoto;
        private boolean showIcons;
        private boolean showSkillLevels;
    }
}
