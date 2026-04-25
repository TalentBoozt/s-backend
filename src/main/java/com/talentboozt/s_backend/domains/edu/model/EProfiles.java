package com.talentboozt.s_backend.domains.edu.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.talentboozt.s_backend.domains.edu.dto.EEducationDTO;
import com.talentboozt.s_backend.domains.edu.dto.EExperienceDTO;
import com.talentboozt.s_backend.domains.edu.dto.ENotificationSettingsDTO;
import com.talentboozt.s_backend.domains.edu.dto.EPrivacySettingsDTO;
import com.talentboozt.s_backend.domains.edu.dto.ESocialLinksDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_profiles")
public class EProfiles {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String userId;
    
    private String firstName;
    private String lastName;
    private String publicEmail;
    private String publicPhone;
    private String avatarUrl;
    private String bio;
    
    private ESocialLinksDTO socialLinks;
    
    @Indexed
    private String industry;
    private String company;
    private String jobTitle;
    
    private EExperienceDTO[] experience;
    private EEducationDTO[] education;
    
    @Indexed
    private String[] skills;
    private String[] languages;
    private String[] interests;
    
    private EPrivacySettingsDTO privacySettings;
    private ENotificationSettingsDTO notificationSettings;
    
    @CreatedDate
    private Instant createdAt;
    
    @LastModifiedDate
    private Instant updatedAt;

    @org.springframework.data.annotation.Transient
    private Integer totalStudents;
    @org.springframework.data.annotation.Transient
    private Integer totalCourses;
    @org.springframework.data.annotation.Transient
    private Double rating;
}
