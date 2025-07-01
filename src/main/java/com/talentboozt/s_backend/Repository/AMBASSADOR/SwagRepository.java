package com.talentboozt.s_backend.Repository.AMBASSADOR;

import com.talentboozt.s_backend.Model.AMBASSADOR.SwagModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SwagRepository extends MongoRepository<SwagModel, String> {
    boolean existsByAmbassadorIdAndTaskId(String ambassadorId, String taskId);
}
