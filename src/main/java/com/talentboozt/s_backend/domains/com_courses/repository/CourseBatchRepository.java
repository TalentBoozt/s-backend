package com.talentboozt.s_backend.domains.com_courses.repository;

import com.talentboozt.s_backend.domains.com_courses.model.CourseBatchModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseBatchRepository extends MongoRepository<CourseBatchModel, String> {
    List<CourseBatchModel> findByCourseId(String courseId);
    Optional<CourseBatchModel> findTopByCourseIdOrderByStartDateDesc(String id);
    boolean existsByCourseId(String id);
}
