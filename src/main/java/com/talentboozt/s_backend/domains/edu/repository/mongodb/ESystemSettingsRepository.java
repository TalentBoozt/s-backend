package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import com.talentboozt.s_backend.domains.edu.model.ESystemSettings;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ESystemSettingsRepository extends MongoRepository<ESystemSettings, String> {
    Optional<ESystemSettings> findByCategory(String category);
}
