package com.talentboozt.s_backend.domains.plat_courses.repository;

import com.talentboozt.s_backend.domains.plat_courses.model.CourseCouponsModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CourseCouponsRepository extends MongoRepository<CourseCouponsModel, String> {
    Optional<CourseCouponsModel> findByToken(String token);
    List<CourseCouponsModel> findByUserId(String userId);
    List<CourseCouponsModel> findByStatus(CourseCouponsModel.Status status);
    List<CourseCouponsModel> findByTaskIdAndStatus(String id, CourseCouponsModel.Status status);
    boolean existsByUserIdAndTaskId(String employeeId, String id);
    Optional<CourseCouponsModel> findByCode(String code);
}
