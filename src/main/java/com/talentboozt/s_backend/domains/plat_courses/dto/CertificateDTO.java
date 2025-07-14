package com.talentboozt.s_backend.domains.plat_courses.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CertificateDTO {
    private String certificateId;           // UUID or generated ID
    private String type;                    // "system" or "trainer"
    private String url;                     // Firebase storage URL
    private String issuedBy;                // "system" or trainer's userId
    private String issuedDate;              // ISO 8601 date
    private String fileName;                // Optional display name
    private String description;             // Optional info
    private boolean delivered;              // If sent to the user (email/sent flag)
    private boolean linkedinShared;         // If user shared to linkedin (linkedin flag)
}
