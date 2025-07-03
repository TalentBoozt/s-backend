package com.talentboozt.s_backend.DTO.PLAT_COURSES;

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
