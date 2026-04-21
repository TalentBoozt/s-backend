package com.talentboozt.s_backend.domains.edu.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.talentboozt.s_backend.domains.edu.enums.ERoles;
import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    
    @JsonIgnore
    private String passwordHash;
    private String ssoProvider;
    private String ssoProviderId;
    @Builder.Default
    private Boolean isEmailVerified = false;
    @Builder.Default
    private Boolean isPhoneVerified = false;
    private String displayName;
    private String avatarUrl;
    @Builder.Default
    private Boolean isActive = true;
    @Builder.Default
    private Boolean isBanned = false;
    private String banReason;
    private Instant lastLoginAt;
    private ERoles[] roles;

    @Builder.Default
    private ESubscriptionPlan plan = ESubscriptionPlan.FREE;

    @Builder.Default
    private ESubscriptionStatus subscriptionStatus = ESubscriptionStatus.ACTIVE;
    private String emailVerificationToken;
    private String passwordResetToken;
    private Instant passwordResetExpiry;
    
    @CreatedDate
    private Instant createdAt;
    
    @LastModifiedDate
    private Instant updatedAt;
    
    @Builder.Default
    private Boolean isMfaEnabled = false;
}
