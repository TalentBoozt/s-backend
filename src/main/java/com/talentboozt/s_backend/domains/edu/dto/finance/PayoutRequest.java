package com.talentboozt.s_backend.domains.edu.dto.finance;

import com.talentboozt.s_backend.domains.edu.enums.EPayoutMethod;
import lombok.Data;

@Data
public class PayoutRequest {
    private Double amount;
    private String currency; // Default USD
    private EPayoutMethod method;
    private String bankDetails; // Encrypted ideally
    private String paypalEmail;
}
