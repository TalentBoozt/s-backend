package com.talentboozt.s_backend.domains.com_courses.model;

import com.talentboozt.s_backend.domains.com_courses.dto.RecModuleDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Setter
@Document(collection = "recorded_courses")
public class RecordedCourseModel {
    @Id
    private String id;
    private String title;
    private String subtitle;
    private String description;
    private String courseType = "recorded";  // fixed for recorded courses
    private String price;
    private boolean isPublished;
    private String createdAt;
    private String updatedAt;
    @Field("modules")
    private List<RecModuleDTO> modules;  // modules could still exist for recorded courses, but they would be fixed
    private String image;  // A visual representation of the course
    private List<String> skills;  // Can still keep skills
    private List<String> requirements;  // For any prerequisites
}
