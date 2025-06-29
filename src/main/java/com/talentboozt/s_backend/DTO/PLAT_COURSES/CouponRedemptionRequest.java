package com.talentboozt.s_backend.DTO.PLAT_COURSES;

import lombok.Data;

@Data
public class CouponRedemptionRequest {
    private String token;
    private String userId;
    private String courseId;
    private String installmentId;
}
