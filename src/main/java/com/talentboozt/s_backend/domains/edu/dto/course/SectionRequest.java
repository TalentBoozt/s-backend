package com.talentboozt.s_backend.domains.edu.dto.course;

import lombok.Data;

@Data
public class SectionRequest {
    private String title;
    private String description;
    private Integer order;
}
