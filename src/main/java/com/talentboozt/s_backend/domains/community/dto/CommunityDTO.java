package com.talentboozt.s_backend.domains.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityDTO {
    private String id;
    private String name;
    private String description;
    private String icon;
    private long memberCount;
    private boolean isJoined;
}