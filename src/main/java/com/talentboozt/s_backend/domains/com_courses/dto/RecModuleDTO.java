package com.talentboozt.s_backend.domains.com_courses.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecModuleDTO {
    private String id;
    private String title;
    private String description;

    private boolean freePreview;

    @Field("lectures")
    private List<RecLectureDTO> lectures;

    private int order;
    private String createdAt;
}
