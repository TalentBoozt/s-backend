package com.talentboozt.s_backend.domains.edu.career.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "career_paths")
public class CareerPathDocument {

    @Id
    private String id;

    @Indexed
    private String slug;

    private String title;
    private String description;
    private String targetSalaryRange;
    private List<String> requiredSkills;
    private List<String> courseReferences;

    public CareerPathDocument() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTargetSalaryRange() { return targetSalaryRange; }
    public void setTargetSalaryRange(String targetSalaryRange) { this.targetSalaryRange = targetSalaryRange; }

    public List<String> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills; }

    public List<String> getCourseReferences() { return courseReferences; }
    public void setCourseReferences(List<String> courseReferences) { this.courseReferences = courseReferences; }
}
