package com.talentboozt.s_backend.domains.ambassador.repository;

import com.talentboozt.s_backend.domains.ambassador.model.SwagModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SwagRepository extends MongoRepository<SwagModel, String> {
    boolean existsByAmbassadorIdAndTaskId(String ambassadorId, String taskId);
}
