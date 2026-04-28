package com.talentboozt.s_backend.domains.subscription.repository.mongodb;

import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import com.talentboozt.s_backend.domains.subscription.model.FeatureFlag;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeatureFlagRepository extends MongoRepository<FeatureFlag, String> {
    Optional<FeatureFlag> findByPlanAndFeatureKey(ESubscriptionPlan plan, String featureKey);
    List<FeatureFlag> findAllByPlan(ESubscriptionPlan plan);
}
