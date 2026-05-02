package com.talentboozt.s_backend.domains.finance_planning.repository.mongodb;

import com.talentboozt.s_backend.domains.finance_planning.models.FinFinancialSnapshot;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FinFinancialSnapshotRepository extends MongoRepository<FinFinancialSnapshot, String> {
    List<FinFinancialSnapshot> findByOrganizationId(String organizationId);

    List<FinFinancialSnapshot> findByOrganizationIdAndProjectId(String organizationId, String projectId);

    List<FinFinancialSnapshot> findByOrganizationIdAndProjectIdAndScenarioId(String organizationId, String projectId,
            String scenarioId);

    Optional<FinFinancialSnapshot> findByOrganizationIdAndProjectIdAndMonth(String organizationId, String projectId,
            String month);

    Optional<FinFinancialSnapshot> findByOrganizationIdAndProjectIdAndScenarioIdAndMonth(String organizationId,
            String projectId,
            String scenarioId, String month);
}