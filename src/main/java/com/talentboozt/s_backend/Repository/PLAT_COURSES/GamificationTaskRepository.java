package com.talentboozt.s_backend.Repository.PLAT_COURSES;

import com.talentboozt.s_backend.Model.PLAT_COURSES.GamificationTaskModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GamificationTaskRepository extends MongoRepository<GamificationTaskModel, String> {
    List<GamificationTaskModel> findByTypeOrderByPriorityDesc(String category);
}
