package com.talentboozt.s_backend.domains.plat_courses.model;

import com.talentboozt.s_backend.domains.plat_courses.dto.CourseEnrollment;
import com.talentboozt.s_backend.domains.plat_courses.dto.RecordedCourseEnrollment;
import lombok.*;
import org.checkerframework.checker.units.qual.N;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor

@Document(collection = "portal_emp_courses")
public class EmpCoursesModel {
    @Id
    private String id;
    private String employeeId;
    private String employeeName;
    private String email;
    private String phone;
    private String timezone;
    @Field("courses")
    private List<CourseEnrollment> courses;
    @Field("recordedCourses")
    private List<RecordedCourseEnrollment> recordedCourses;

    // mock for test
    public EmpCoursesModel(String id) {
        this.id = id;
    }
}
