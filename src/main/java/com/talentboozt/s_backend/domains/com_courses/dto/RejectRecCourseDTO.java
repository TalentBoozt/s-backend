package com.talentboozt.s_backend.domains.com_courses.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RejectRecCourseDTO {
    private String code; // INVALID_CONTENT, INVALID_FORMAT, MISSING_CONTENT, OTHER
    private String reason;
    private String courseId;
    private String courseName;
    private String rejectedAt; // timestamp
}
