package com.talentboozt.s_backend.domains.plat_courses.repository.mongodb;

import com.talentboozt.s_backend.domains.plat_courses.model.CourseCertificateModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseCertificateRepository extends MongoRepository<CourseCertificateModel, String> {
    Optional<List<CourseCertificateModel>> findAllByCourseId(String courseId);
    Optional<List<CourseCertificateModel>> findAllByEmployeeId(String employeeId);
    Optional<CourseCertificateModel> findByCertificateId(String certificateId);
    Optional<List<CourseCertificateModel>> findAllByType(String type);
    Optional<List<CourseCertificateModel>> findAllByDelivered(boolean delivered);
}
