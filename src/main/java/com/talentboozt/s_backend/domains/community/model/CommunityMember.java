package com.talentboozt.s_backend.domains.community.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "community_members")
public class CommunityMember {
    @Id
    private String id;
    private String communityId;
    private String userId;
    private MemberRole role;
    private LocalDateTime joinedAt;
    private boolean isBanned;
    private LocalDateTime bannedAt;
    private String bannedReason;

    public enum MemberRole {
        ADMIN, MODERATOR, MEMBER
    }
}
