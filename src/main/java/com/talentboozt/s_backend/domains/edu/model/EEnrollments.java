package com.talentboozt.s_backend.domains.edu.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_enrollments")
@CompoundIndexes({
    @CompoundIndex(name = "user_course_idx", def = "{'userId': 1, 'courseId': 1}", unique = true)
})
public class EEnrollments {
    @Id
    private String id;
    
    @Indexed
    private String courseId;
    
    @Indexed
    private String userId;
    
    @Indexed
    private String workspaceId;
    
    private Integer progress = 0;
    
    private Integer completedLessons = 0;
    private Integer totalLessons = 0;
    
    private Integer completedSections = 0;
    private Integer totalSections = 0;
    
    private Integer completedQuizzes = 0;
    private Integer totalQuizzes = 0;
    
    private Integer completedAssignments = 0;
    private Integer totalAssignments = 0;
    
    private Integer completedProjects = 0;
    private Integer totalProjects = 0;
    
    @Indexed
    private Boolean completed = false;
    
    private String[] completedLessonIds;
    private String lastAccessedLessonId;
    private Instant lastAccessedAt;
    
    private Instant completedAt;
    
    @Indexed
    private Instant enrolledAt;
    
    private Integer currentStreak = 0;
    private Integer longestStreak = 0;
    private Instant lastStreakDate;
    
    @Indexed
    private String source; // MARKETPLACE, GIFT, ENTERPRISE, COUPON
    
    private String createdBy;
    private String updatedBy;
    
    @CreatedDate
    private Instant createdAt;
    
    @LastModifiedDate
    private Instant updatedAt;
}
