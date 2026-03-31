package com.talentboozt.s_backend.domains.edu.dto.monetization;

import lombok.Data;

@Data
public class CourseCheckoutRequest {
    private String userId;
    private String courseId;
    private String affiliateId;
}
