package com.talentboozt.s_backend.shared.mail.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class BankPaymentDTO {
    private String companyId;
    private String name;
    private String country;
    private String address;
    private String phone;
    private String slipUrl;
}
