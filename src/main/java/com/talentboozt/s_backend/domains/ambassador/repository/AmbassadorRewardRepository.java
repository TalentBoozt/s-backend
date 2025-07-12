package com.talentboozt.s_backend.domains.ambassador.repository;

import com.talentboozt.s_backend.domains.ambassador.model.AmbassadorRewardModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AmbassadorRewardRepository extends MongoRepository<AmbassadorRewardModel, String> {
    Iterable<AmbassadorRewardModel> findAllByAmbassadorId(String ambassadorId);
    boolean existsByAmbassadorIdAndTaskId(String id, String id1);
}
