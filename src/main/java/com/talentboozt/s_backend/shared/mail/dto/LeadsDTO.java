package com.talentboozt.s_backend.shared.mail.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeadsDTO {
    String name;
    String email;
    String serviceType;
    String ctaType;
    String focusArea;
    String message;
}
