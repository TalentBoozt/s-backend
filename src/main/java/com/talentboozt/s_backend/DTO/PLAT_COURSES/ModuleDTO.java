package com.talentboozt.s_backend.DTO.PLAT_COURSES;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class ModuleDTO {
    @Id
    private String id;
    private String name;
    private String duration;
    private String installmentId;
    private String date;
    private String start;
    private String end;
    private String paid;
    private String meetingLink;
    private String status;
}
