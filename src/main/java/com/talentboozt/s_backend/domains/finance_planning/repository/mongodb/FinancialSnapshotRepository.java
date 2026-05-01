package com.talentboozt.s_backend.domains.finance_planning.repository.mongodb;

import com.talentboozt.s_backend.domains.finance_planning.models.FinancialSnapshot;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FinancialSnapshotRepository extends MongoRepository<FinancialSnapshot, String> {
    List<FinancialSnapshot> findByOrganizationIdAndProjectId(String organizationId, String projectId);

    Optional<FinancialSnapshot> findByOrganizationIdAndProjectIdAndMonth(String organizationId, String projectId,
            String month);
}