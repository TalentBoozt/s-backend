package com.talentboozt.s_backend.domains.edu.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.talentboozt.s_backend.domains.edu.enums.ELessonType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_lessons")
public class ELessons {
    @Id
    private String id;
    
    @Indexed
    private String courseId;
    
    @Indexed
    private String sectionId;
    
    private Integer order;
    private String title;
    private String description;
    private String contentUrl;
    private String textContent;
    private String markdownContent;
    
    @Indexed
    private ELessonType type;
    
    private Integer duration; // in minutes
    @Builder.Default
    private Boolean isFreePreview = false;
    @Builder.Default
    private Boolean isPublished = true;
    @Builder.Default
    private Boolean isDrmProtected = false;
    private String videoThumbnail;
    private String[] attachments; // File URLs
    
    private String createdBy;
    private String updatedBy;
    
    @CreatedDate
    private Instant createdAt;
    
    @LastModifiedDate
    private Instant updatedAt;
}
