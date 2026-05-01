package com.talentboozt.s_backend.domains.finance_planning.repository.mongodb;

import com.talentboozt.s_backend.domains.finance_planning.models.FinFinancialSnapshot;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FinFinancialSnapshotRepository extends MongoRepository<FinFinancialSnapshot, String> {
    List<FinFinancialSnapshot> findByOrganizationIdAndProjectId(String organizationId, String projectId);

    Optional<FinFinancialSnapshot> findByOrganizationIdAndProjectIdAndMonth(String organizationId, String projectId,
            String month);
}