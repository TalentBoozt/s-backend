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
public class CourseUpdateDTO {
    private CourseProgressDTO courseProgress;
    private List<ModuleProgressDTO> moduleProgress;
}
