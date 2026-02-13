package com.talentboozt.s_backend.domains.reputation.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaderboardEntry {
    private String userId;
    private long totalScore;
    private long articleScore;
    private long communityScore;
    private int rank;
    private String name;
    private String avatar;
}
