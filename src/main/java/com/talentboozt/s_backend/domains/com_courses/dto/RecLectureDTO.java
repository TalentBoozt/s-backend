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
public class RecLectureDTO {
    private String id;
    private String title;
    private String description;

    private String notes;

    @Field("materials")
    private List<RecMaterialDTO> materials;

    private String videoUrl;
    private int duration; // in seconds

    private String createdAt;
}
