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
    private String notes;  // Any notes for the lecture
    @Field("materials")
    private List<RecMaterialDTO> materials;  // Static files uploaded with lectures
    private String videoUrl;  // Link to the recorded video
    private int duration;  // Duration of the lecture
    private String createdAt;  // Created date of the lecture
}
