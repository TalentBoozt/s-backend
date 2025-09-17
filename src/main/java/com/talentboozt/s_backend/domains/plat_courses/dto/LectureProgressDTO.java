package com.talentboozt.s_backend.domains.plat_courses.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LectureProgressDTO {
    private String lectureId;
    private String lectureTitle;
    private boolean watched;
    private int watchDuration; // seconds watched
    private int totalDuration; // full length of lecture
    private String lastWatchedAt; // ISO date for resume feature
}