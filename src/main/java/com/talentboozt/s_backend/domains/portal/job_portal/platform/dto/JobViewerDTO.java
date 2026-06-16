package com.talentboozt.s_backend.domains.portal.job_portal.platform.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class JobViewerDTO {
    private String id;
    private String employeeId;
    private String name;
    private String status;
    private String date;
}
