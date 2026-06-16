package com.talentboozt.s_backend.domains.portal.courses.platform.repository.mongodb;

import com.talentboozt.s_backend.domains.portal.courses.platform.model.GamificationTaskModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GamificationTaskRepository extends MongoRepository<GamificationTaskModel, String> {
    List<GamificationTaskModel> findByTypeOrderByPriorityDesc(String category);
}
