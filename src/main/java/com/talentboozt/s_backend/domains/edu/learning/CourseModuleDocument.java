package com.talentboozt.s_backend.domains.edu.learning;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

/**
 * Course Module Catalog Entity.
 * Maps dynamic modules order structures, unlocking credentials, and estimation pacing
 * inside the MongoDB collection "course_modules".
 */
@Document(collection = "course_modules")
public class CourseModuleDocument {

    @Id
    private String id;

    @Indexed
    private String courseId;

    private String title;
    private int orderNumber;
    private double completionPercentage;
    private boolean isUnlocked = true;
    private String estimatedDuration;
    private List<String> lessonReferences;
    private List<String> prerequisites;

    public CourseModuleDocument() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getOrderNumber() { return orderNumber; }
    public void setOrderNumber(int orderNumber) { this.orderNumber = orderNumber; }

    public double getCompletionPercentage() { return completionPercentage; }
    public void setCompletionPercentage(double completionPercentage) { this.completionPercentage = completionPercentage; }

    public boolean isUnlocked() { return isUnlocked; }
    public void setUnlocked(boolean unlocked) { isUnlocked = unlocked; }

    public String getEstimatedDuration() { return estimatedDuration; }
    public void setEstimatedDuration(String estimatedDuration) { this.estimatedDuration = estimatedDuration; }

    public List<String> getLessonReferences() { return lessonReferences; }
    public void setLessonReferences(List<String> lessonReferences) { this.lessonReferences = lessonReferences; }

    public List<String> getPrerequisites() { return prerequisites; }
    public void setPrerequisites(List<String> prerequisites) { this.prerequisites = prerequisites; }
}
