package com.talentboozt.s_backend.domains.plat_courses.repository;

import com.talentboozt.s_backend.domains.plat_courses.model.GamificationTaskModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GamificationTaskRepository extends MongoRepository<GamificationTaskModel, String> {
    List<GamificationTaskModel> findByTypeOrderByPriorityDesc(String category);
}
