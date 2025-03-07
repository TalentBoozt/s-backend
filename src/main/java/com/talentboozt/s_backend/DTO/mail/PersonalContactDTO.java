package com.talentboozt.s_backend.DTO.mail;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PersonalContactDTO {
    private String name;
    private String fromEmail;
    private String toEmail;
    private String subject;
    private String message;
}
