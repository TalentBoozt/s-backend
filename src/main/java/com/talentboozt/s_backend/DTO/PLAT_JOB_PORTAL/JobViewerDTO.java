package com.talentboozt.s_backend.DTO.PLAT_JOB_PORTAL;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class JobViewerDTO {
    private String id;
    private String employeeId;
    private String name;
    private String status;
    private String date;
}
