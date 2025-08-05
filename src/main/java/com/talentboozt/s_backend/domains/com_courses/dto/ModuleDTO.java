package com.talentboozt.s_backend.domains.com_courses.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
