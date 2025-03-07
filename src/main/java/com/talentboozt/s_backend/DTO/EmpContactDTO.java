package com.talentboozt.s_backend.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class EmpContactDTO {
    @Id
    private String id;
    private String phone;
    private String email;
    private String address;
    private String city;
    private String country;
    private String zipCode;
    private String website;
}
