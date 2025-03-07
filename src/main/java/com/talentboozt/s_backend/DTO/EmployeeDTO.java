package com.talentboozt.s_backend.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeDTO {
    private String firstname;
    private String lastname;
    private String occupation;
    private String image;
    private String coverImage;
    private String dob;
    private String email;
    private String resume;
    private String intro;
    private String skills; //id
    private String experiences; //id
    private String education; //id
    private String contactInfo; //id
    private Object savedJobs; //ids
    private Object accountNotifications;
    private Object marketingNotifications;
    private Object profileCompleted;
    private String profileStatus;
    private String companyId;
}
