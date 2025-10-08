package com.talentboozt.s_backend.domains.payment.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequestDto {
    private String courseId;
    private String courseName;
    private String learnerId;
    private String trainerId;
    private String companyId;
    private BigDecimal grossAmount;
    private BigDecimal netAmount;
    private String currency;
    private String splitType;     // trainer-led / platform-led / promotion
    private String paymentMethod;
    private String transactionId;
}

