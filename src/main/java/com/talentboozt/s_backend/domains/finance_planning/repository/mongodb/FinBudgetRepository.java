package com.talentboozt.s_backend.domains.finance_planning.repository.mongodb;

import com.talentboozt.s_backend.domains.finance_planning.models.FinBudget;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface FinBudgetRepository extends MongoRepository<FinBudget, String> {
    List<FinBudget> findByOrganizationIdAndProjectId(String organizationId, String projectId);
    Page<FinBudget> findByOrganizationIdAndProjectId(String organizationId, String projectId, Pageable pageable);
    Optional<FinBudget> findByOrganizationIdAndProjectIdAndCategory(String organizationId, String projectId, String category);
}