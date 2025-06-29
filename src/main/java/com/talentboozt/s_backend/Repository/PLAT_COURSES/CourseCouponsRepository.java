package com.talentboozt.s_backend.Repository.PLAT_COURSES;

import com.talentboozt.s_backend.Model.PLAT_COURSES.CourseCouponsModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CourseCouponsRepository extends MongoRepository<CourseCouponsModel, String> {
    Optional<CourseCouponsModel> findByToken(String token);
    List<CourseCouponsModel> findByUserId(String userId);
    List<CourseCouponsModel> findByStatus(CourseCouponsModel.Status status);
}
