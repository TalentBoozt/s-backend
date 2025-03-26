package com.talentboozt.s_backend.DTO.PLAT_COURSES;

import com.talentboozt.s_backend.DTO.COM_COURSES.InstallmentDTO;
import com.talentboozt.s_backend.DTO.COM_COURSES.ModuleDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CourseEnrollment {
    private String courseId;
    private String courseName;
    private String description;
    private String image;
    private String organizer;
    private String enrollmentDate;
    private String status;  // e.g., "enrolled", "completed", etc.
    private List<InstallmentDTO> installment;
    private List<ModuleDTO> modules;
}
