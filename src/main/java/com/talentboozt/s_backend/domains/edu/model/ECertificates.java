package com.talentboozt.s_backend.domains.edu.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
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
@Document(collection = "edu_certificates")
public class ECertificates {
    @Id
    private String id;
    
    @Indexed
    private String courseId;
    
    @Indexed
    private String userId;
    
    @Indexed
    private String creatorId; // Person who issued the course
    
    private String courseName;
    private String recipientName;
    
    @Indexed(unique = true)
    private String certificateId; // public verification ID
    
    private String url;
    private String templateId;
    
    private Boolean isVerified = true;
    private String shareableLink;
    
    @CreatedDate
    private Instant issuedAt;
}
