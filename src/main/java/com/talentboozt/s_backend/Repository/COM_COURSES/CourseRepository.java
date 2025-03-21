package com.talentboozt.s_backend.Repository.COM_COURSES;

import com.talentboozt.s_backend.Model.COM_COURSES.CourseModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CourseRepository extends MongoRepository<CourseModel, String> {
    List<CourseModel> findByCompanyId(String companyId);
}
