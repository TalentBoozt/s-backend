package com.talentboozt.s_backend.Repository;

import com.talentboozt.s_backend.Model.FeatureModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FeatureRepository extends MongoRepository<FeatureModel, String> {
}
