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
    
    @Builder.Default
    private Integer progress = 0;
    
    @Builder.Default
    private Integer completedLessons = 0;
    @Builder.Default
    private Integer totalLessons = 0;
    
    @Builder.Default
    private Integer completedSections = 0;
    @Builder.Default
    private Integer totalSections = 0;
    
    @Builder.Default
    private Integer completedQuizzes = 0;
    @Builder.Default
    private Integer totalQuizzes = 0;
    
    @Builder.Default
    private Integer completedAssignments = 0;
    @Builder.Default
    private Integer totalAssignments = 0;
    
    @Builder.Default
    private Integer completedProjects = 0;
    @Builder.Default
    private Integer totalProjects = 0;
    
    @Indexed
    @Builder.Default
    private Boolean completed = false;
    
    private String[] completedLessonIds;
    private String lastAccessedLessonId;
    private Instant lastAccessedAt;
    @Builder.Default
    private Long totalWatchTime = 0L; // in seconds
    
    private Instant completedAt;
    
    @Indexed
    private Instant enrolledAt;
    
    @Builder.Default
    private Integer currentStreak = 0;
    @Builder.Default
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
