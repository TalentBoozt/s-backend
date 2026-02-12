package com.talentboozt.s_backend.domains.ambassador.repository.mongodb;

import com.talentboozt.s_backend.domains.ambassador.model.AmbassadorSessionModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AmbassadorSessionRepository extends MongoRepository<AmbassadorSessionModel, String> {
    Iterable<AmbassadorSessionModel> findByAmbassadorId(String id);

    int countByAmbassadorIdAndType(String id, String hosted);
}
