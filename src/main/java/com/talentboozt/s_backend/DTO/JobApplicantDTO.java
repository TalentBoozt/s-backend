package com.talentboozt.s_backend.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class JobApplicantDTO {
    private String id;
    private String employeeId;
    private String name;
    private String email;
    private String phone;
    private String location;
    private String resume;
    private String coverLetter;
    private String status;
    private String date;
}
