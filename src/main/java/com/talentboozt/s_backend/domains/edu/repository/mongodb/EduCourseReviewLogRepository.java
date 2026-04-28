package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import com.talentboozt.s_backend.domains.edu.model.EduCourseReviewLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EduCourseReviewLogRepository extends MongoRepository<EduCourseReviewLog, String> {
    List<EduCourseReviewLog> findByCourseId(String courseId);
}
