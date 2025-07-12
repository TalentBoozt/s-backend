package com.talentboozt.s_backend.domains.common.repository;

import com.talentboozt.s_backend.domains.common.model.FeatureModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FeatureRepository extends MongoRepository<FeatureModel, String> {
}
