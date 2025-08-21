package com.talentboozt.s_backend.domains.plat_courses.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CertificateDetailsDTO {
    private String userId;
    private String userName;
    private String email;

    private String courseId;
    private String courseName;

    private String certificateId;
    private String fileName;
    private String type;
    private String url;
    private String issuedBy;
    private String issuedDate;
    private boolean delivered;
    private boolean linkedinShared;
}
