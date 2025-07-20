package com.talentboozt.s_backend.domains.plat_courses.repository;

import com.talentboozt.s_backend.domains.plat_courses.model.EmpCoursesModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface EmpCoursesRepository extends MongoRepository<EmpCoursesModel, String> {
    List<EmpCoursesModel> findAllByEmployeeId(String employeeId);
    void deleteByEmployeeId(String employeeId);
    List<EmpCoursesModel> findByCoursesCourseId(String courseId);
    List<EmpCoursesModel> findByCoursesCourseIdAndCoursesBatchId(String batchId, String courseId);
    Optional<EmpCoursesModel> findByEmployeeId(String employeeId);
}
