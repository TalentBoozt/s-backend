package com.talentboozt.s_backend.domains.portal.common.repository.mongodb;

import com.talentboozt.s_backend.domains.portal.common.model.FeatureModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FeatureRepository extends MongoRepository<FeatureModel, String> {
}
