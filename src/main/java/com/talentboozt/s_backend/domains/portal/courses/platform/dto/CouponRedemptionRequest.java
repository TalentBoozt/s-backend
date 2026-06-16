package com.talentboozt.s_backend.domains.portal.courses.platform.dto;

import lombok.Data;

@Data
public class CouponRedemptionRequest {
    private String token;
    private String userId;
    private String courseId;
    private String installmentId;
}
