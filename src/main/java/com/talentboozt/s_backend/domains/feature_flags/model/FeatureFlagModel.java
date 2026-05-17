package com.talentboozt.s_backend.domains.feature_flags.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "feature_flags")
public class FeatureFlagModel {
    @Id
    private String id;
    private String key; // E.g., "AI_JOB_GEN", "ADVANCED_ANALYTICS", "MULTI_TEAM"
    private String name;
    private String description;
    
    private boolean globalEnabled;
    private List<String> enabledForRoles; // RECRUITER, APPLICANT, ADMIN
    private List<String> enabledForTiers; // FREE, PRO, ENTERPRISE
    
    private List<String> whitelistedOrgIds;
    private List<String> whitelistedUserIds;
}
