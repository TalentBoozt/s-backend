package com.talentboozt.s_backend.domains.plat_courses.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDetailsDTO {
    private String userId;
    private String userName;
    private String email;

    private String courseId;
    private String courseName;

    private String installmentId;
    private String installmentName;
    private String price;
    private String currency;
    private String paid;
    private String paymentMethod;
    private String paymentDate;
}
