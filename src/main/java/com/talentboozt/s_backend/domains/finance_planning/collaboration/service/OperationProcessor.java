package com.talentboozt.s_backend.domains.finance_planning.collaboration.service;

import com.talentboozt.s_backend.domains.finance_planning.collaboration.models.CollaborationOperation;
import com.talentboozt.s_backend.domains.finance_planning.models.*;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.*;
import com.talentboozt.s_backend.domains.finance_planning.services.FinFinancialComputationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperationProcessor {
    private final SimpMessagingTemplate messagingTemplate;
    private final ConflictResolver conflictResolver;
    private final FinFinancialComputationService computationService;
    
    private final FinSalesPlanRepository salesPlanRepository;
    private final FinBudgetRepository budgetRepository;
    private final FinAssumptionRepository assumptionRepository;
    private final FinPricingModelRepository pricingModelRepository;

    @Transactional
    public void processOperation(CollaborationOperation op) {
        log.info("Processing operation: {} type: {}", op.getId(), op.getType());
        
        boolean applied = false;
        
        switch (op.getType()) {
            case UPDATE_CELL:
                applied = handleUpdateCell(op);
                break;
            case UPDATE_ASSUMPTION:
                applied = handleUpdateAssumption(op);
                break;
            case APPLY_SCENARIO:
                applied = handleApplyScenario(op);
                break;
            default:
                log.warn("Unsupported operation type: {}", op.getType());
        }

        if (applied) {
            // 2. Trigger formula engine
            computationService.recomputeFinancials(op.getOrganizationId(), op.getProjectId());
            
            // 3. Broadcast to others
            messagingTemplate.convertAndSend("/topic/project/" + op.getProjectId() + "/operations", op);
            
            // Also broadcast state update if needed
            messagingTemplate.convertAndSend("/topic/project/" + op.getProjectId() + "/state_update", "REFRESH");
        }
    }

    private boolean handleUpdateCell(CollaborationOperation op) {
        String path = op.getPath();
        if (path.startsWith("sales.")) {
            String tier = path.substring(6);
            Optional<FinSalesPlan> planOpt = salesPlanRepository.findByOrganizationIdAndProjectIdAndMonth(
                op.getOrganizationId(), op.getProjectId(), op.getMonth());
            
            FinSalesPlan plan = planOpt.orElse(new FinSalesPlan());
            if (planOpt.isEmpty()) {
                plan.setOrganizationId(op.getOrganizationId());
                plan.setProjectId(op.getProjectId());
                plan.setMonth(op.getMonth());
            }
            
            // Real app: Versioned Optimistic Locking (LWW)
            if (plan.getVersion() != null && op.getVersion() != null && op.getVersion() < plan.getVersion()) {
                log.warn("Stale operation rejected: op version {} < entity version {}", op.getVersion(), plan.getVersion());
                return false;
            }
            
            plan.getUserCounts().put(tier, ((Number) op.getValue()).intValue());
            salesPlanRepository.save(plan);
            return true;
        } else if (path.startsWith("budget.")) {
            String category = path.substring(7);
            Optional<FinBudget> budgetOpt = budgetRepository.findByOrganizationIdAndProjectIdAndCategory(
                op.getOrganizationId(), op.getProjectId(), category);
            
            FinBudget budget = budgetOpt.orElse(new FinBudget());
            if (budgetOpt.isEmpty()) {
                budget.setOrganizationId(op.getOrganizationId());
                budget.setProjectId(op.getProjectId());
                budget.setCategory(category);
            }
            
            // Real app: Versioned Optimistic Locking (LWW)
            if (budget.getVersion() != null && op.getVersion() != null && op.getVersion() < budget.getVersion()) {
                log.warn("Stale budget operation rejected: op version {} < entity version {}", op.getVersion(), budget.getVersion());
                return false;
            }
            
            budget.getMonthlyAllocations().put(op.getMonth(), ((Number) op.getValue()).doubleValue());
            budgetRepository.save(budget);
            return true;
        } else if (path.startsWith("pricing.")) {
            String[] parts = path.split("\\.");
            String field = parts[1]; // price or commissionPercent
            String tier = parts[2];
            
            Optional<FinPricingModel> modelOpt = pricingModelRepository.findByOrganizationIdAndProjectIdAndTier(
                op.getOrganizationId(), op.getProjectId(), tier);
            
            FinPricingModel model = modelOpt.orElse(new FinPricingModel());
            if (modelOpt.isEmpty()) {
                model.setOrganizationId(op.getOrganizationId());
                model.setProjectId(op.getProjectId());
                model.setTier(tier);
            }
            
            // Real app: Versioned Optimistic Locking (LWW)
            if (model.getVersion() != null && op.getVersion() != null && op.getVersion() < model.getVersion()) {
                log.warn("Stale pricing operation rejected: op version {} < entity version {}", op.getVersion(), model.getVersion());
                return false;
            }
            
            if ("price".equals(field)) {
                model.setPrice(((Number) op.getValue()).doubleValue());
            } else {
                model.setCommissionPercent(((Number) op.getValue()).doubleValue());
            }
            pricingModelRepository.save(model);
            return true;
        }
        return false;
    }

    private boolean handleApplyScenario(CollaborationOperation op) {
        log.info("Applying scenario: {} to project: {}", op.getValue(), op.getProjectId());
        // Logic to apply scenario overrides...
        // For now we just return true to trigger a broadcast which forces clients to reload
        return true;
    }

    private boolean handleUpdateAssumption(CollaborationOperation op) {
        Optional<FinAssumption> assumptionOpt = assumptionRepository.findByOrganizationIdAndProjectIdAndKey(
            op.getOrganizationId(), op.getProjectId(), op.getPath());
        
        FinAssumption assumption = assumptionOpt.orElse(new FinAssumption());
        if (assumptionOpt.isEmpty()) {
            assumption.setOrganizationId(op.getOrganizationId());
            assumption.setProjectId(op.getProjectId());
            assumption.setKey(op.getPath());
        }
        
        assumption.setValue(op.getValue().toString()); // Assumes string value for now
        assumptionRepository.save(assumption);
        return true;
    }
}
