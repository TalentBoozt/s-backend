package com.talentboozt.s_backend.domains.edu.dto.finance;

import com.talentboozt.s_backend.domains.edu.enums.EPayoutMethod;
import lombok.Data;

import jakarta.validation.constraints.Positive;

@Data
public class PayoutRequest {
    @Positive
    private Double amount;
    private String currency; // Default USD
    private EPayoutMethod method;
    private String bankDetails; // Encrypted ideally
    private String paypalEmail;
}
