package com.talentboozt.s_backend.Repository.AMBASSADOR;

import com.talentboozt.s_backend.Model.AMBASSADOR.AmbassadorSessionModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AmbassadorSessionRepository extends MongoRepository<AmbassadorSessionModel, String> {
    Iterable<AmbassadorSessionModel> findByAmbassadorId(String id);
}
