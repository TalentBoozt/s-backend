package com.talentboozt.s_backend.domains.organization.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "organizations")
public class OrganizationModel {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String name;
    
    @Indexed(unique = true)
    private String slug;
    
    private String logoUrl;
    private String websiteUrl;
    private String description;
    
    private String ownerId;
    
    private OrganizationSettings settings;
    
    private List<OrganizationMember> members;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private String subscriptionPlan; // FREE, PRO, ENTERPRISE
    private boolean active;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrganizationSettings {
        private boolean allowMemberInvitation;
        private boolean autoApproveMembers;
        private Map<String, Object> customMetadata;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrganizationMember {
        private String userId;
        private String role; // OWNER, ADMIN, RECRUITER, MEMBER
        private LocalDateTime joinedAt;
    }
}
