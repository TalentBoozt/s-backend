package com.talentboozt.s_backend.domains.edu.dto.monetization;

import lombok.Data;

@Data
public class CheckoutRequest {
    private String userId;
    private String planName;
    private String billingCycle;
}
