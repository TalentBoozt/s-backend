package com.talentboozt.s_backend.domains.portal.courses.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntry {
    private String employeeId;
    private double bestScore;
    private String lastSubmissionAt;
}
