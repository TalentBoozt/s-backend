package com.talentboozt.s_backend.Repository.common;

import com.talentboozt.s_backend.Model.common.FeatureModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FeatureRepository extends MongoRepository<FeatureModel, String> {
}
