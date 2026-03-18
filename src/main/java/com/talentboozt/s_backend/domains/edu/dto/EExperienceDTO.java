package com.talentboozt.s_backend.domains.edu.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EExperienceDTO {
    private String id;
    private String company;
    private String jobTitle;
    private String startDate;
    private String endDate;
    private String[] responsibilities;
    private String[] achievements;
    private String[] projects;
}
