package com.talentboozt.s_backend.domains.edu.dto.monetization;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class CheckoutRequest {
    @NotBlank
    private String userId;
    private String planName;
    private String billingCycle;
}
