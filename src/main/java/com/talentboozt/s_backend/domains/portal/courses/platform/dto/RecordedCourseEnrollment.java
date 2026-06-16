package com.talentboozt.s_backend.domains.portal.courses.platform.dto;

import com.talentboozt.s_backend.domains.portal.courses.community.dto.InstallmentDTO;
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
    private InstallmentDTO installment;
}
