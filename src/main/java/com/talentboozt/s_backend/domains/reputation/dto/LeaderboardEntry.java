package com.talentboozt.s_backend.domains.reputation.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaderboardEntry {
    private String userId;
    private long score;
    private int rank;
    private String name; // To be populated if needed
}
