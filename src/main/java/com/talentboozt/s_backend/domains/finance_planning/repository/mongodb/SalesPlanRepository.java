package com.talentboozt.s_backend.domains.finance_planning.repository.mongodb;

import com.talentboozt.s_backend.domains.finance_planning.models.SalesPlan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalesPlanRepository extends MongoRepository<SalesPlan, String> {
    List<SalesPlan> findByOrganizationIdAndProjectId(String organizationId, String projectId);
    Optional<SalesPlan> findByOrganizationIdAndProjectIdAndMonth(String organizationId, String projectId, String month);
}