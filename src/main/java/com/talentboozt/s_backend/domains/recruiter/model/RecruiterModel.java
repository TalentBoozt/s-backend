package com.talentboozt.s_backend.domains.recruiter.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "recruiters")
public class RecruiterModel {
    @Id
    private String id;
    private String userId; // Link to CredentialsModel
    private String organizationId; // Primary organization
    
    private String jobTitle;
    private String department;
    
    private RecruiterSettings settings;
    private RecruiterPreferences preferences;
    
    private Instant createdAt;
    private Instant updatedAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecruiterSettings {
        private boolean emailNotifications;
        private boolean mobileNotifications;
        private List<String> specializedIndustries;
        private Map<String, Object> uiPreferences;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecruiterPreferences {
        private List<String> defaultPipelineStages;
        private String defaultNotificationDigest; // DAILY, WEEKLY, NONE
        private boolean autoShortlistHighMatch;
    }
}
