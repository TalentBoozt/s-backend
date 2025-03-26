package com.talentboozt.s_backend.DTO.COM_COURSES;

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
    private String paymentLink;
    private String bank;
    private String accountNb;
    private String branch;
    private String holder;
    private String paid;
}
