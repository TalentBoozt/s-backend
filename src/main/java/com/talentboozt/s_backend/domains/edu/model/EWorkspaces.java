package com.talentboozt.s_backend.domains.edu.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.talentboozt.s_backend.domains.edu.dto.EWProfileDTO;
import com.talentboozt.s_backend.domains.edu.dto.EWSettingsDTO;
import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import com.talentboozt.s_backend.domains.edu.enums.EWorkspaceType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_workspaces")
public class EWorkspaces {
    @Id
    private String id;
    
    @Indexed
    private String ownerId;
    
    @Indexed
    private EWorkspaceType type;
    
    @Indexed(unique = true)
    private String domain;
    
    private ESubscriptionPlan plan;
    
    private String name;
    private String description;
    private String logoUrl;
    
    @Builder.Default
    private Boolean isActive = true;
    
    private Integer maxMembers;
    @Builder.Default
    private Integer totalMembers = 0;
    @Builder.Default
    private Integer totalCourses = 0;
    
    private String[] departmentTags;
    
    private EWSettingsDTO settings;
    private EWProfileDTO profile;
    
    @CreatedDate
    private Instant createdAt;
    
    @LastModifiedDate
    private Instant updatedAt;
}