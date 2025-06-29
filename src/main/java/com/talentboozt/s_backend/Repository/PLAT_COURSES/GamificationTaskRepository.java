package com.talentboozt.s_backend.Repository.PLAT_COURSES;

import com.talentboozt.s_backend.Model.PLAT_COURSES.GamificationTaskModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GamificationTaskRepository extends MongoRepository<GamificationTaskModel, String> {
}
