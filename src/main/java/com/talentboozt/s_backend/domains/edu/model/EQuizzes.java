package com.talentboozt.s_backend.domains.edu.model;

import com.talentboozt.s_backend.domains.edu.dto.evaluation.EQuestionDTO;
import com.talentboozt.s_backend.domains.edu.enums.EQuizType;
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
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_quizzes")
public class EQuizzes {
    @Id
    private String id;
    
    @Indexed
    private String courseId;
    
    @Indexed
    private String sectionId;
    
    private String title;
    private String description;
    
    private EQuizType type;
    
    private Integer durationLimit; // in minutes
    private Double passingScore; // percentage
    
    private List<EQuestionDTO> questions;
    
    private Boolean isPublished;
    private Integer allowRetakes; // number of times, 0 for infinite
    
    private String createdBy;
    
    @CreatedDate
    private Instant createdAt;
    
    @LastModifiedDate
    private Instant updatedAt;
}
