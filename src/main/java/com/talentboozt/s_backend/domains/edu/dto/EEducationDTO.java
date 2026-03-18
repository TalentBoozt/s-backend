package com.talentboozt.s_backend.domains.edu.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EEducationDTO {
    private String id;
    private String degree;
    private String fieldOfStudy;
    private String institution;
    private String startDate;
    private String endDate;
    private String gpa;
    private String[] honors;
    private String[] activities;
    private String[] certifications;
}
