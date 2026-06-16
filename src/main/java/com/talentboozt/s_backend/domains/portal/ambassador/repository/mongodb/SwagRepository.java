package com.talentboozt.s_backend.domains.portal.ambassador.repository.mongodb;

import com.talentboozt.s_backend.domains.portal.ambassador.model.SwagModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SwagRepository extends MongoRepository<SwagModel, String> {
    boolean existsByAmbassadorIdAndTaskId(String ambassadorId, String taskId);
}
