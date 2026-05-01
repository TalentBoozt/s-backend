package com.talentboozt.s_backend.domains.finance_planning.services;

import com.talentboozt.s_backend.domains.finance_planning.dtos.FinanceStateDto;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.*;
import lombok.RequiredArgsConstructor;
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
}
