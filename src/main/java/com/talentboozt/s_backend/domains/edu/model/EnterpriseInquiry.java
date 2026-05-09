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
@Document(collection = "edu_enterprise_inquiries")
public class EnterpriseInquiry {
    @Id
    private String id;
    
    @Indexed
    private String userId;
    
    private String companyName;
    private String contactEmail;
    private String contactPerson;
    private String phoneNumber;
    
    private Integer expectedMembers;
    private Integer expectedCourses;
    private String requirements;
    private String additionalNotes;
    
    @Builder.Default
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED, CONTACTED
    
    private String adminNotes;
    private String processedBy;
    private Instant processedAt;
    
    @CreatedDate
    private Instant createdAt;
    
    @LastModifiedDate
    private Instant updatedAt;
}
