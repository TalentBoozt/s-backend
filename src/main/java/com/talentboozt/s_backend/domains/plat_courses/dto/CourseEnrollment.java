package com.talentboozt.s_backend.domains.plat_courses.dto;

import com.talentboozt.s_backend.domains.com_courses.dto.InstallmentDTO;
import com.talentboozt.s_backend.domains.com_courses.dto.ModuleDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CourseEnrollment {
    private String courseId;
    private String courseName;
    private String overview;
    private String image;
    private String organizer;
    private String enrollmentDate;
    private String status;  // e.g., "enrolled", "completed", etc.
    private List<InstallmentDTO> installment;
    private List<ModuleDTO> modules;
    private List<CertificateDTO> certificates;
}
