package com.talentboozt.s_backend.domains.reputation.repository.mongodb;

import com.talentboozt.s_backend.domains.reputation.model.UserReputation;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserReputationRepository extends MongoRepository<UserReputation, String> {
    Optional<UserReputation> findByUserId(String userId);

    java.util.List<UserReputation> findByUserIdIn(java.util.List<String> userIds);
}
