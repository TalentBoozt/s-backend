package com.talentboozt.s_backend.domains.finance_planning.repository.mongodb;

import com.talentboozt.s_backend.domains.finance_planning.models.FinSalesPlan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FinSalesPlanRepository extends MongoRepository<FinSalesPlan, String> {
    List<FinSalesPlan> findByOrganizationIdAndProjectId(String organizationId, String projectId);
    Optional<FinSalesPlan> findByOrganizationIdAndProjectIdAndMonth(String organizationId, String projectId, String month);
}