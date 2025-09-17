package com.talentboozt.s_backend.domains.com_courses.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecordedCourseReviewDTO {
    private String reviewId;
    private String employeeId;
    private String employeeName;
    private String comment;
    private int rating; // 1 to 5
    private String reviewDate;
}

