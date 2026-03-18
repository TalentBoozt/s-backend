package com.talentboozt.s_backend.domains.edu.dto.course;

import com.talentboozt.s_backend.domains.edu.enums.ECourseLevel;
import com.talentboozt.s_backend.domains.edu.enums.ECourseType;
import lombok.Data;

@Data
public class CourseRequest {
    private String title;
    private String description;
    private String shortDescription;
    private ECourseType type;
    private String language;
    private ECourseLevel level;
    private String[] categories;
    private String[] subCategories;
    private Double price;
    private String currency;
    private Boolean isPrivate;
}
