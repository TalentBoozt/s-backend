package com.talentboozt.s_backend.domains.com_courses.repository;

import com.talentboozt.s_backend.domains.com_courses.model.CourseModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CourseRepository extends MongoRepository<CourseModel, String> {
    List<CourseModel> findByCompanyId(String companyId);
}
