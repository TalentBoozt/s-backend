package com.talentboozt.s_backend.Repository.PLAT_COURSES;

import com.talentboozt.s_backend.Model.PLAT_COURSES.EmpCoursesModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EmpCoursesRepository extends MongoRepository<EmpCoursesModel, String> {
    List<EmpCoursesModel> findByEmployeeId(String employeeId);
    void deleteByEmployeeId(String employeeId);
}
