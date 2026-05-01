package com.talentboozt.s_backend.domains.finance_planning.repository.mongodb;

import com.talentboozt.s_backend.domains.finance_planning.models.FinPricingModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FinPricingModelRepository extends MongoRepository<FinPricingModel, String> {
    List<FinPricingModel> findByOrganizationIdAndProjectId(String organizationId, String projectId);
    Optional<FinPricingModel> findByOrganizationIdAndProjectIdAndTier(String organizationId, String projectId, String tier);
}