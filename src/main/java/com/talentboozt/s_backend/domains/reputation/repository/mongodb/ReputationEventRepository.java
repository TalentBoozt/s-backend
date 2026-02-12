package com.talentboozt.s_backend.domains.reputation.repository.mongodb;

import com.talentboozt.s_backend.domains.reputation.model.ReputationEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ReputationEventRepository extends MongoRepository<ReputationEvent, String> {
    List<ReputationEvent> findByUserId(String userId);
}
