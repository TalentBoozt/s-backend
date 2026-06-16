package com.talentboozt.s_backend.domains.portal.job_portal.platform.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
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
