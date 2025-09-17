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
public class RecordedCourseEnrollment {
    private String courseId;
    private String courseName;
    private String status; // "purchased", "in-progress", "completed"
    private String organizer;
    private String enrollmentDate;
    private int overallProgress; // 0-100
    private CourseProgressDTO courseProgress;
    private List<ModuleProgressDTO> moduleProgress;
    private ReviewDTO review;
    private String image;
    private String description;
    private List<CertificateDTO> certificates;
}
