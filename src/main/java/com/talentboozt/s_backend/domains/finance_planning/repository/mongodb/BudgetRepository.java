package com.talentboozt.s_backend.domains.finance_planning.repository.mongodb;

import com.talentboozt.s_backend.domains.finance_planning.models.Budget;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends MongoRepository<Budget, String> {
    List<Budget> findByOrganizationIdAndProjectId(String organizationId, String projectId);
    Optional<Budget> findByOrganizationIdAndProjectIdAndCategory(String organizationId, String projectId, String category);
}