package com.talentboozt.s_backend.domains.plat_courses.dto;

import lombok.Data;

@Data
public class CouponRedemptionRequest {
    private String token;
    private String userId;
    private String courseId;
    private String installmentId;
}
