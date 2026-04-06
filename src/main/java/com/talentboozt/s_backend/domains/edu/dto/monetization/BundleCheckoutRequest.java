package com.talentboozt.s_backend.domains.edu.dto.monetization;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class BundleCheckoutRequest {
    @NotBlank
    private String userId;
    @NotBlank
    private String bundleId;
    /** Optional coupon code to apply at checkout */
    private String couponCode;
}
