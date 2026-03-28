package com.talentboozt.s_backend.domains.edu.dto.monetization;

import lombok.Data;

@Data
public class CoursePurchaseConfirmRequest {
    private String userId;
    private String sessionId;
}
