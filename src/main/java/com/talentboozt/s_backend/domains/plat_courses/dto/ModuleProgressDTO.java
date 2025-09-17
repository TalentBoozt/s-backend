package com.talentboozt.s_backend.domains.plat_courses.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModuleProgressDTO {
    private String moduleId;
    private String moduleTitle;
    private int completedLectures;
    private int totalLectures;
    private List<LectureProgressDTO> lectures;
    private boolean isCompleted;
}
