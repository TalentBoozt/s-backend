package com.talentboozt.s_backend.DTO.common.mail;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CVRequestDTO {
    private String name;
    private String email;
    private String dob;
    private String careerStage;
    private String jobTitle;
    private String link;
    private String message;
}
