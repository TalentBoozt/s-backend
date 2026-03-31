package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.talentboozt.s_backend.domains.edu.model.EValidationReports;
import java.util.List;
import java.util.Optional;

@Repository
public interface EValidationReportsRepository extends MongoRepository<EValidationReports, String> {
    List<EValidationReports> findByCourseId(String courseId);
    Optional<EValidationReports> findFirstByCourseIdOrderByCreatedAtDesc(String courseId);
}
