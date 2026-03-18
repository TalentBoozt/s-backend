package com.talentboozt.s_backend.domains.edu.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.talentboozt.s_backend.domains.edu.enums.EReportReason;
import com.talentboozt.s_backend.domains.edu.enums.EReportStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_reports")
public class EReports {
    @Id
    private String id;
    
    @Indexed
    private String reporterId;
    
    @Indexed
    private String targetEntityId; // course ID, user ID, module ID
    
    private String entityType; // COURSE, USER, REVIEW
    
    @Indexed
    private EReportReason reason;
    
    private String description;
    
    @Indexed
    private EReportStatus status;
    
    private String resolutionNotes;
    private String resolvedBy;
    
    @CreatedDate
    private Instant createdAt;
    
    @LastModifiedDate
    private Instant updatedAt;
}
