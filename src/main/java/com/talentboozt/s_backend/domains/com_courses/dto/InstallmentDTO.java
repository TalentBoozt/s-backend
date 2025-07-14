package com.talentboozt.s_backend.domains.com_courses.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class InstallmentDTO {
    @Id
    private String id;
    private String name;
    private String currency;
    private String price;
    private String discountedPrice;
    private String paymentLink;
    private String productId;
    private String priceId;
    private String priceType; // default, discounted
    private String bank;
    private String accountNb;
    private String branch;
    private String holder;
    private String paid; //unpaid, paid, pending, failed
    private String paymentMethod;
    private String requestDate;
    private String paymentDate;
}
