package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.talentboozt.s_backend.domains.edu.model.EUserPreferences;
import java.util.Optional;

@Repository
public interface EUserPreferencesRepository extends MongoRepository<EUserPreferences, String> {
    Optional<EUserPreferences> findByUserId(String userId);
}
