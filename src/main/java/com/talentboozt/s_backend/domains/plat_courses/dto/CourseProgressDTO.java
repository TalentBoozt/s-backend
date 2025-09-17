package com.talentboozt.s_backend.domains.plat_courses.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseProgressDTO {
    private int completedLectures;     // total completed lectures
    private int totalLectures;         // total lectures in course
    private int completedModules;      // optional: how many modules are fully completed
    private int totalModules;
    private int progressPercent;       // optional convenience field
    private int totalWatchTimeSeconds; // how much time user has spent watching
}