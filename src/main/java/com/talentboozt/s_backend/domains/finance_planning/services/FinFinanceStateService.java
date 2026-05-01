package com.talentboozt.s_backend.domains.finance_planning.services;

import com.talentboozt.s_backend.domains.finance_planning.dtos.FinanceStateDto;
import com.talentboozt.s_backend.domains.finance_planning.models.FinBudget;
import com.talentboozt.s_backend.domains.finance_planning.models.FinSalesPlan;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FinFinanceStateService {
    private final FinAssumptionRepository assumptionRepository;
    private final FinSalesPlanRepository salesPlanRepository;
    private final FinPricingModelRepository pricingModelRepository;
    private final FinBudgetRepository budgetRepository;
    private final FinFinancialSnapshotRepository financialSnapshotRepository;
    private final FinScenarioRepository scenarioRepository;

    public FinanceStateDto getFullState(String organizationId, String projectId) {
        return FinanceStateDto.builder()
                .assumptions(assumptionRepository.findByOrganizationIdAndProjectId(organizationId, projectId))
                .sales(salesPlanRepository.findByOrganizationIdAndProjectId(organizationId, projectId))
                .pricing(pricingModelRepository.findByOrganizationIdAndProjectId(organizationId, projectId))
                .budget(budgetRepository.findByOrganizationIdAndProjectId(organizationId, projectId))
                .financials(financialSnapshotRepository.findByOrganizationIdAndProjectId(organizationId, projectId))
                .scenarios(scenarioRepository.findByOrganizationIdAndProjectId(organizationId, projectId))
                .build();
    }

    public Page<FinSalesPlan> getPaginatedSales(String organizationId, String projectId, int page, int size) {
        return salesPlanRepository.findByOrganizationIdAndProjectId(organizationId, projectId, PageRequest.of(page, size));
    }

    public Page<FinBudget> getPaginatedBudget(String organizationId, String projectId, int page, int size) {
        return budgetRepository.findByOrganizationIdAndProjectId(organizationId, projectId, PageRequest.of(page, size));
    }
}
