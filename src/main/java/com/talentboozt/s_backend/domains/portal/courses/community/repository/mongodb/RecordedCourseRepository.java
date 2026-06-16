package com.talentboozt.s_backend.domains.portal.courses.community.repository.mongodb;

import com.talentboozt.s_backend.domains.portal.courses.community.model.RecordedCourseModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordedCourseRepository extends MongoRepository<RecordedCourseModel, String> {
    List<RecordedCourseModel> findByPublishedTrue();

    List<RecordedCourseModel> findByPublishedTrueAndApprovedTrue();

    List<RecordedCourseModel> findByCompanyId(String companyId);

    List<RecordedCourseModel> findByTrainerId(String trainerId);
}
