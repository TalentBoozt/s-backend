package com.talentboozt.s_backend.domains.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityDTO {
    private String id;
    private String name;
    private String description;
    private String icon;
    private String bannerImage;
    private String creatorId;
    private List<String> adminIds;
    private List<String> moderatorIds;
    private String privacy; // PUBLIC, PRIVATE, RESTRICTED
    private String createdAt;
    private String updatedAt;
    private String rules;
    private String category;
    private List<String> tags;
    private boolean isVerified;

    // Computed fields for current user
    private long memberCount;
    private boolean isJoined;
    private String userRole; // ADMIN, MODERATOR, MEMBER, or null if not joined
}
