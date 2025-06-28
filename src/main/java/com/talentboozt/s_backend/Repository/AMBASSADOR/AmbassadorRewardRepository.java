package com.talentboozt.s_backend.Repository.AMBASSADOR;

import com.talentboozt.s_backend.Model.AMBASSADOR.AmbassadorRewardModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AmbassadorRewardRepository extends MongoRepository<AmbassadorRewardModel, String> {
    Iterable<AmbassadorRewardModel> findAllByAmbassadorId(String ambassadorId);
}
