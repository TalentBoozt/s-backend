package com.talentboozt.s_backend.domains.portal.courses.community.repository.mongodb;

import com.talentboozt.s_backend.domains.portal.courses.community.model.CourseModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CourseRepository extends MongoRepository<CourseModel, String> {
    List<CourseModel> findByCompanyId(String companyId);
}
