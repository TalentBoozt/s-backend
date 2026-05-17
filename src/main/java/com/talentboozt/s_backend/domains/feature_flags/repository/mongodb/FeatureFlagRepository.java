package com.talentboozt.s_backend.domains.feature_flags.repository.mongodb;

import com.talentboozt.s_backend.domains.feature_flags.model.FeatureFlagModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface FeatureFlagRepository extends MongoRepository<FeatureFlagModel, String> {
    Optional<FeatureFlagModel> findByKey(String key);
}
