package com.talentboozt.s_backend.domains.finance_planning.models;

import java.time.Instant;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "fin_users")
public class FinUser {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String email;
    
    @JsonIgnore
    private String passwordHash;
    
    private String displayName;
    
    @Builder.Default
    private Boolean isActive = true;
    
    private String[] roles;
    private java.util.List<java.util.Map<String, String>> organizations;
    private String activeWorkspaceId;
    
    private Instant lastLoginAt;
    
    @CreatedDate
    private Instant createdAt;
    
    @LastModifiedDate
    private Instant updatedAt;
}
