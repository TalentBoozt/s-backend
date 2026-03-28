package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.talentboozt.s_backend.domains.edu.model.EEnrollments;

import java.util.List;
import java.util.Optional;

@Repository
public interface EEnrollmentsRepository extends MongoRepository<EEnrollments, String> {
    Optional<EEnrollments> findByUserIdAndCourseId(String userId, String courseId);
    List<EEnrollments> findByUserId(String userId);
    List<EEnrollments> findByCourseId(String courseId);
}
