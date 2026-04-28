package com.talentboozt.s_backend.domains.edu.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_course_review_logs")
public class EduCourseReviewLog {
    @Id
    private String id;
    
    @Indexed
    private String courseId;
    
    @Indexed
    private String reviewerId;
    
    private String action; // APPROVED, REJECTED, STARTED_REVIEW
    private String reason;
    
    @CreatedDate
    private Instant createdAt;
}
