package com.talentboozt.s_backend.domains.plat_courses.dto;

import com.talentboozt.s_backend.domains.com_courses.dto.InstallmentDTO;
import com.talentboozt.s_backend.domains.com_courses.dto.ModuleDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseEnrollment {
    private String courseId;
    private String courseName;
    private String batchId;
    private String overview;
    private String image;
    private String organizer;
    private String enrollmentDate;
    private String status;  // e.g., "enrolled", "completed", etc.
    private List<InstallmentDTO> installment;
    private List<ModuleDTO> modules;
    private List<CertificateDTO> certificates;
}
