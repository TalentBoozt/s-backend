package com.talentboozt.s_backend.domains.edu.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.talentboozt.s_backend.domains.edu.dto.EValidationBreackdownDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_validation_reports")
public class EValidationReports {
    @Id
    private String id;
    
    @Indexed
    private String courseId;
    
    @Indexed
    private String userId;
    
    @Indexed
    private String reviewerId;
    
    private Double aiScore;
    
    @Indexed
    private String status;
    
    private EValidationBreackdownDTO breakdown;
    private String createdBy;
    
    @CreatedDate
    private Instant createdAt;
}
