package com.talentboozt.s_backend.domains.edu.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.talentboozt.s_backend.domains.edu.enums.ERoles;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_user")
public class EUser {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String email;
    
    @Indexed(sparse = true)
    private String phone;
    
    private String passwordHash;
    private String ssoProvider;
    private String ssoProviderId;
    private Boolean isEmailVerified = false;
    private Boolean isPhoneVerified = false;
    private String displayName;
    private String avatarUrl;
    private Boolean isActive = true;
    private Boolean isBanned = false;
    private String banReason;
    private Instant lastLoginAt;
    private ERoles[] roles;
    
    @CreatedDate
    private Instant createdAt;
    
    @LastModifiedDate
    private Instant updatedAt;
}
