package com.talentboozt.s_backend.DTO.COM_COURSES;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class ModuleDTO {
    @Id
    private String id;
    private String name;
    private String description;
    private String duration;
    private String installmentId;
    private String date;
    private String start;
    private String end;
    private String utcStart;
    private String utcEnd;
    private String trainerTimezone;
    private String paid;
    private String meetingLink;
    private String status;
    private boolean onetimePayment;
}
