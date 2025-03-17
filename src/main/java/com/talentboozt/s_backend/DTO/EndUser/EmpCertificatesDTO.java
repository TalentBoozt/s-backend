package com.talentboozt.s_backend.DTO.EndUser;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class EmpCertificatesDTO {
    @Id
    private String id;
    private String name;
    private String organization;
    private String date;
    private String certificateId;
    private String certificateUrl;
}
