package com.talentboozt.s_backend.domains.edu.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_assignments")
public class EAssignments {
    @Id
    private String id;
    
    @Indexed
    private String courseId;
    
    @Indexed
    private String sectionId;
    
    private String title;
    private String description;
    private String instructions;
    
    private String[] attachmentUrls;
    
    private Double maxScore;
    private Double weightage; // How much it contributes to final grade
    private Instant dueDate;
    
    private Boolean isPublished;
    
    private String createdBy;
    
    @CreatedDate
    private Instant createdAt;
    
    @LastModifiedDate
    private Instant updatedAt;
}
