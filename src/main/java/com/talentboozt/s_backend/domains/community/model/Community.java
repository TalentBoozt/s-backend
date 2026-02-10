package com.talentboozt.s_backend.domains.community.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "communities")
public class Community {
    @Id
    private String id;
    private String name;
    private String description;
    private String icon;
    private String bannerImage;
    private String creatorId;
    private List<String> adminIds;
    private List<String> moderatorIds;
    private CommunityPrivacy privacy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String rules;
    private String category;
    private List<String> tags;
    private boolean isVerified;

    public enum CommunityPrivacy {
        PUBLIC, PRIVATE, RESTRICTED
    }
}
