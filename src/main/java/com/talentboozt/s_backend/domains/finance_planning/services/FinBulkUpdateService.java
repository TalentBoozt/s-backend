package com.talentboozt.s_backend.domains.finance_planning.services;

import com.talentboozt.s_backend.domains.finance_planning.dtos.BulkUpdateDto;
import com.talentboozt.s_backend.domains.finance_planning.dtos.FinDeltaUpdate;
import com.talentboozt.s_backend.domains.finance_planning.models.*;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.*;
import com.talentboozt.s_backend.shared.security.model.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinBulkUpdateService {
    private final FinAssumptionRepository assumptionRepository;
    private final FinSalesPlanRepository salesPlanRepository;
    private final FinPricingModelRepository pricingModelRepository;
    private final FinBudgetRepository budgetRepository;
    private final FinScenarioRepository scenarioRepository;
    private final FinFinancialComputationService computationService;
    private final ApplicationEventPublisher eventPublisher;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void processBulkUpdate(String organizationId, String projectId, BulkUpdateDto request) {
        if (request == null || request.getOperations() == null) {
            return;
        }

        String userId = getCurrentUserId();
        log.info("Processing {} bulk operations for org: {}, project: {}, user: {}", 
                request.getOperations().size(), organizationId, projectId, userId);

        java.util.List<String> changedFields = new java.util.ArrayList<>();
        for (BulkUpdateDto.BulkOperation op : request.getOperations()) {
            try {
                handleOperation(organizationId, projectId, op);
                broadcastUpdate(projectId, userId, op);
                changedFields.add(op.getType() + "." + op.getAction());
            } catch (Exception e) {
                log.error("Error processing bulk operation: {}", op, e);
                throw new RuntimeException("Bulk update failed: " + e.getMessage(), e);
            }
        }

        // Recompute financials with full context for AI pipeline
        computationService.recomputeFinancials(organizationId, projectId, "base", userId, changedFields);
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) auth.getPrincipal()).getUserId();
        }
        return "anonymous";
    }

    private void broadcastUpdate(String projectId, String userId, BulkUpdateDto.BulkOperation op) {
        Integer version = null;
        if (op.getPayload() != null && op.getPayload().get("version") instanceof Integer) {
            version = (Integer) op.getPayload().get("version");
        }

        FinDeltaUpdate delta = FinDeltaUpdate.builder()
                .type(op.getType())
                .path(op.getType() + "/" + op.getId())
                .value(op.getPayload())
                .version(version)
                .timestamp(Instant.now())
                .userId(userId)
                .build();

        String destination = "/topic/projects/" + projectId + "/updates";
        messagingTemplate.convertAndSend(destination, delta);
        log.debug("Broadcasted update to {}: {}", destination, delta);
    }

    private void handleOperation(String organizationId, String projectId, BulkUpdateDto.BulkOperation op) {
        String type = op.getType();
        String action = op.getAction();
        String id = op.getId();
        java.util.Map<String, Object> payload = op.getPayload();

        switch (type.toLowerCase()) {
            case "assumption":
                handleAssumption(organizationId, projectId, action, id, payload);
                break;
            case "sales":
                handleSales(organizationId, projectId, action, id, payload);
                break;
            case "pricing":
                handlePricing(organizationId, projectId, action, id, payload);
                break;
            case "budget":
                handleBudget(organizationId, projectId, action, id, payload);
                break;
            case "scenario":
                handleScenario(organizationId, projectId, action, id, payload);
                break;
            default:
                throw new IllegalArgumentException("Unsupported operation type: " + type);
        }
    }

    private void handleAssumption(String orgId, String projId, String action, String id, java.util.Map<String, Object> payload) {
        if ("delete".equalsIgnoreCase(action)) {
            assumptionRepository.findById(id).ifPresent(doc -> {
                validateOwnership(doc.getOrganizationId(), doc.getProjectId(), orgId, projId);
                assumptionRepository.delete(doc);
            });
            return;
        }

        FinAssumption doc = "create".equalsIgnoreCase(action) ? new FinAssumption() :
                assumptionRepository.findById(id).orElseThrow(() -> new RuntimeException("Assumption not found: " + id));

        if (!"create".equalsIgnoreCase(action)) {
            validateOwnership(doc.getOrganizationId(), doc.getProjectId(), orgId, projId);
        }

        applyPayload(doc, payload);
        doc.setOrganizationId(orgId);
        doc.setProjectId(projId);
        if (doc.getCreatedAt() == null) doc.setCreatedAt(java.time.Instant.now());
        assumptionRepository.save(doc);
    }

    private void handleSales(String orgId, String projId, String action, String id, java.util.Map<String, Object> payload) {
        if ("delete".equalsIgnoreCase(action)) {
            salesPlanRepository.findById(id).ifPresent(doc -> {
                validateOwnership(doc.getOrganizationId(), doc.getProjectId(), orgId, projId);
                salesPlanRepository.delete(doc);
            });
            return;
        }

        FinSalesPlan doc = "create".equalsIgnoreCase(action) ? new FinSalesPlan() :
                salesPlanRepository.findById(id).orElseThrow(() -> new RuntimeException("Sales plan not found: " + id));

        if (!"create".equalsIgnoreCase(action)) {
            validateOwnership(doc.getOrganizationId(), doc.getProjectId(), orgId, projId);
        }

        applyPayload(doc, payload);
        doc.setOrganizationId(orgId);
        doc.setProjectId(projId);
        if (doc.getCreatedAt() == null) doc.setCreatedAt(java.time.Instant.now());
        salesPlanRepository.save(doc);
    }

    private void handlePricing(String orgId, String projId, String action, String id, java.util.Map<String, Object> payload) {
        if ("delete".equalsIgnoreCase(action)) {
            pricingModelRepository.findById(id).ifPresent(doc -> {
                validateOwnership(doc.getOrganizationId(), doc.getProjectId(), orgId, projId);
                pricingModelRepository.delete(doc);
            });
            return;
        }

        FinPricingModel doc = "create".equalsIgnoreCase(action) ? new FinPricingModel() :
                pricingModelRepository.findById(id).orElseThrow(() -> new RuntimeException("Pricing model not found: " + id));

        if (!"create".equalsIgnoreCase(action)) {
            validateOwnership(doc.getOrganizationId(), doc.getProjectId(), orgId, projId);
        }

        applyPayload(doc, payload);
        doc.setOrganizationId(orgId);
        doc.setProjectId(projId);
        if (doc.getEffectiveDate() == null) doc.setEffectiveDate(java.time.Instant.now());
        pricingModelRepository.save(doc);
    }

    private void handleBudget(String orgId, String projId, String action, String id, java.util.Map<String, Object> payload) {
        if ("delete".equalsIgnoreCase(action)) {
            budgetRepository.findById(id).ifPresent(doc -> {
                validateOwnership(doc.getOrganizationId(), doc.getProjectId(), orgId, projId);
                budgetRepository.delete(doc);
            });
            return;
        }

        FinBudget doc = "create".equalsIgnoreCase(action) ? new FinBudget() :
                budgetRepository.findById(id).orElseThrow(() -> new RuntimeException("Budget not found: " + id));

        if (!"create".equalsIgnoreCase(action)) {
            validateOwnership(doc.getOrganizationId(), doc.getProjectId(), orgId, projId);
        }

        applyPayload(doc, payload);
        doc.setOrganizationId(orgId);
        doc.setProjectId(projId);
        if (doc.getCreatedAt() == null) doc.setCreatedAt(java.time.Instant.now());
        budgetRepository.save(doc);
    }

    private void handleScenario(String orgId, String projId, String action, String id, java.util.Map<String, Object> payload) {
        if ("delete".equalsIgnoreCase(action)) {
            scenarioRepository.findById(id).ifPresent(doc -> {
                validateOwnership(doc.getOrganizationId(), doc.getProjectId(), orgId, projId);
                scenarioRepository.delete(doc);
            });
            return;
        }

        FinScenario doc = "create".equalsIgnoreCase(action) ? new FinScenario() :
                scenarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Scenario not found: " + id));

        if (!"create".equalsIgnoreCase(action)) {
            validateOwnership(doc.getOrganizationId(), doc.getProjectId(), orgId, projId);
        }

        applyPayload(doc, payload);
        doc.setOrganizationId(orgId);
        doc.setProjectId(projId);
        if (doc.getCreatedAt() == null) doc.setCreatedAt(java.time.Instant.now());
        scenarioRepository.save(doc);
    }

    private void validateOwnership(String docOrgId, String docProjId, String reqOrgId, String reqProjId) {
        if (!reqOrgId.equals(docOrgId) || !reqProjId.equals(docProjId)) {
            log.error("Ownership violation: doc({},{}) req({},{})", docOrgId, docProjId, reqOrgId, reqProjId);
            throw new SecurityException("Tenant isolation violation: Unauthorized access to project data");
        }
    }

    private void applyPayload(Object target, java.util.Map<String, Object> payload) {
        try {
            objectMapper.updateValue(target, payload);
        } catch (Exception e) {
            throw new RuntimeException("Failed to apply update payload", e);
        }
    }
}
