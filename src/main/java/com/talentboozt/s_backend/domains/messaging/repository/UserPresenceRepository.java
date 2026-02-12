package com.talentboozt.s_backend.domains.messaging.repository;

import com.talentboozt.s_backend.domains.messaging.model.UserPresence;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPresenceRepository extends MongoRepository<UserPresence, String> {
}
