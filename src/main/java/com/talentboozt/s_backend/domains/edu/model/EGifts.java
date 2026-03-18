package com.talentboozt.s_backend.domains.edu.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.talentboozt.s_backend.domains.edu.enums.EGiftStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_gifts")
public class EGifts {
    @Id
    private String id;
    
    @Indexed
    private String senderId;
    
    @Indexed
    private String recipientEmail;
    
    @Indexed
    private String courseId;
    
    @Indexed(unique = true)
    private String redeemCode;
    
    @Indexed
    private EGiftStatus status;
    
    private String personalMessage;
    
    @CreatedDate
    private Instant createdAt;
    
    @LastModifiedDate
    private Instant updatedAt;
    
    private Instant redeemedAt;
    
    @Indexed(expireAfterSeconds = 0)
    private Instant expiresAt; // Will be set to 30 days dynamically for DB auto-purging
}
