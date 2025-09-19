package com.talentboozt.s_backend.domains.com_courses.repository;

import com.talentboozt.s_backend.domains.com_courses.model.RecordedCourseModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordedCourseRepository extends MongoRepository<RecordedCourseModel, String> {
    List<RecordedCourseModel> findByPublishedTrue();

    List<RecordedCourseModel> findByPublishedTrueAndApprovedTrue();
}
