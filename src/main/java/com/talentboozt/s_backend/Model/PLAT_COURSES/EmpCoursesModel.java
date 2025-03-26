package com.talentboozt.s_backend.Model.PLAT_COURSES;

import com.talentboozt.s_backend.DTO.PLAT_COURSES.CourseEnrollment;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Setter
@ToString

@Document(collection = "portal_emp_courses")
public class EmpCoursesModel {
    @Id
    private String id;
    private String employeeId;
    @Field("courses")
    private List<CourseEnrollment> courses;
}
