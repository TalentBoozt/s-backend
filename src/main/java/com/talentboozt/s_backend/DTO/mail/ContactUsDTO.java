package com.talentboozt.s_backend.DTO.mail;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ContactUsDTO {
    private String name;
    private String email;
    private String subject;
    private String message;
}
