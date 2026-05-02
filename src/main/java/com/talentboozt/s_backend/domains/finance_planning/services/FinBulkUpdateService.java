package com.talentboozt.s_backend.domains.finance_planning.services;

import com.talentboozt.s_backend.domains.finance_planning.dtos.BulkUpdateDto;
import com.talentboozt.s_backend.domains.finance_planning.dtos.FinDeltaUpdate;
import com.talentboozt.s_backend.domains.finance_planning.exception.FinVersionConflictException;
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
import java.util.ArrayList;
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
    private final FinValidatorService validatorService;
    private final ApplicationEventPublisher eventPublisher;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final io.micrometer.core.instrument.MeterRegistry meterRegistry;
    private final FinAuditService auditService;

    @Transactional
    public void processBulkUpdate(String organizationId, String projectId, BulkUpdateDto request) {
        if (request == null || request.getOperations() == null) {
            return;
        }

        String operationId = java.util.UUID.randomUUID().toString().substring(0, 8);
        org.slf4j.MDC.put("operationId", operationId);
        
        String userId = getCurrentUserId();
        log.info("EVENT=BULK_UPDATE_START opCount={} orgId={} projId={} userId={}", 
                request.getOperations().size(), organizationId, projectId, userId);

        java.util.List<String> changedFields = new java.util.ArrayList<>();
        java.util.Set<String> affectedMonths = new java.util.HashSet<>();
        
        try {
            for (BulkUpdateDto.BulkOperation op : request.getOperations()) {
                try {
                    // Capture old state for audit
                    Object oldState = getOldState(organizationId, projectId, op);
                    Object result = handleOperation(organizationId, projectId, op);
                    
                    // Audit logging using Service
                    auditService.log(
                        organizationId, 
                        projectId, 
                        userId, 
                        op.getAction().toUpperCase(), 
                        op.getType().toUpperCase(), 
                        op.getId(), 
                        oldState, 
                        op.getPayload()
                    );

                    Integer newVersion = (result instanceof UpdateDetails) ? ((UpdateDetails) result).getVersion() : (Integer) result;
                    if (result instanceof UpdateDetails details && details.getMonth() != null) {
                        affectedMonths.add(details.getMonth());
                    }
                    broadcastUpdate(projectId, userId, op, newVersion);
                    changedFields.add(op.getType() + "." + op.getAction());
                    
                    log.debug("OP_SUCCESS type={} action={} id={}", op.getType(), op.getAction(), op.getId());
                } catch (Exception e) {
                    log.error("OP_FAILURE type={} action={} id={} error={}", op.getType(), op.getAction(), op.getId(), e.getMessage());
                    throw new RuntimeException("Bulk update failed: " + e.getMessage(), e);
                }
            }

            // Recompute financials incrementally
            computationService.recomputeFinancials(organizationId, projectId, "base", userId, changedFields, new ArrayList<>(affectedMonths));
            
            log.info("EVENT=BULK_UPDATE_SUCCESS opCount={} orgId={} projId={}", 
                    request.getOperations().size(), organizationId, projectId);
        } finally {
            org.slf4j.MDC.remove("operationId");
        }
    }

    @lombok.Value
    private static class UpdateDetails {
        Integer version;
        String month;
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) auth.getPrincipal()).getUserId();
        }
        return "anonymous";
    }

    private void broadcastUpdate(String projectId, String userId, BulkUpdateDto.BulkOperation op, Integer newVersion) {
        FinDeltaUpdate delta = FinDeltaUpdate.builder()
                .type(op.getType())
                .path(op.getType() + "/" + op.getId())
                .value(op.getPayload())
                .version(newVersion)
                .timestamp(Instant.now())
                .userId(userId)
                .build();

        String destination = "/topic/projects/" + projectId + "/updates";
        messagingTemplate.convertAndSend(destination, delta);
        
        meterRegistry.counter("websocket.events.sent", "type", op.getType()).increment();
        
        log.debug("Broadcasted update to {}: {}", destination, delta);
    }

    private Object handleOperation(String organizationId, String projectId, BulkUpdateDto.BulkOperation op) {
        String type = op.getType();
        String action = op.getAction();
        String id = op.getId();
        Integer expectedVersion = op.getExpectedVersion();
        java.util.Map<String, Object> payload = op.getPayload();

        switch (type.toLowerCase()) {
            case "assumption":
                return handleAssumption(organizationId, projectId, action, id, expectedVersion, payload);
            case "sales":
                return handleSales(organizationId, projectId, action, id, expectedVersion, payload);
            case "pricing":
                return handlePricing(organizationId, projectId, action, id, expectedVersion, payload);
            case "budget":
                return handleBudget(organizationId, projectId, action, id, expectedVersion, payload);
            case "scenario":
                handleScenario(organizationId, projectId, action, id, expectedVersion, payload);
                return null;
            default:
                throw new IllegalArgumentException("Unsupported operation type: " + type);
        }
    }

    private Object handleAssumption(String orgId, String projId, String action, String id, Integer expectedVersion,
            java.util.Map<String, Object> payload) {
        if ("delete".equalsIgnoreCase(action)) {
            assumptionRepository.findById(id).ifPresent(doc -> {
                validateOwnership(doc.getOrganizationId(), doc.getProjectId(), orgId, projId);
                assumptionRepository.delete(doc);
            });
            return null;
        }

        FinAssumption doc = "create".equalsIgnoreCase(action) ? new FinAssumption()
                : assumptionRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Assumption not found: " + id));

        if (!"create".equalsIgnoreCase(action)) {
            validateOwnership(doc.getOrganizationId(), doc.getProjectId(), orgId, projId);
            validateVersion(doc, expectedVersion);
        }

        applyPayload(doc, payload);
        validatorService.validate(doc);
        doc.setOrganizationId(orgId);
        doc.setProjectId(projId);
        if (doc.getCreatedAt() == null)
            doc.setCreatedAt(java.time.Instant.now());
        assumptionRepository.save(doc);
        return doc.getVersion();
    }

    private Object handleSales(String orgId, String projId, String action, String id, Integer expectedVersion,
            java.util.Map<String, Object> payload) {
        if ("delete".equalsIgnoreCase(action)) {
            salesPlanRepository.findById(id).ifPresent(doc -> {
                validateOwnership(doc.getOrganizationId(), doc.getProjectId(), orgId, projId);
                salesPlanRepository.delete(doc);
            });
            return null;
        }

        FinSalesPlan doc = "create".equalsIgnoreCase(action) ? new FinSalesPlan()
                : salesPlanRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Sales plan not found: " + id));

        if (!"create".equalsIgnoreCase(action)) {
            validateOwnership(doc.getOrganizationId(), doc.getProjectId(), orgId, projId);
            validateVersion(doc, expectedVersion);
        }

        applyPayload(doc, payload);
        validatorService.validate(doc);
        doc.setOrganizationId(orgId);
        doc.setProjectId(projId);
        if (doc.getCreatedAt() == null)
            doc.setCreatedAt(java.time.Instant.now());
        salesPlanRepository.save(doc);
        return new UpdateDetails(doc.getVersion(), doc.getMonth());
    }

    private Object handlePricing(String orgId, String projId, String action, String id, Integer expectedVersion,
            java.util.Map<String, Object> payload) {
        if ("delete".equalsIgnoreCase(action)) {
            pricingModelRepository.findById(id).ifPresent(doc -> {
                validateOwnership(doc.getOrganizationId(), doc.getProjectId(), orgId, projId);
                pricingModelRepository.delete(doc);
            });
            return null;
        }

        FinPricingModel doc = "create".equalsIgnoreCase(action) ? new FinPricingModel()
                : pricingModelRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Pricing model not found: " + id));

        if (!"create".equalsIgnoreCase(action)) {
            validateOwnership(doc.getOrganizationId(), doc.getProjectId(), orgId, projId);
            validateVersion(doc, expectedVersion);
        }

        applyPayload(doc, payload);
        validatorService.validate(doc);
        doc.setOrganizationId(orgId);
        doc.setProjectId(projId);
        if (doc.getEffectiveDate() == null)
            doc.setEffectiveDate(java.time.Instant.now());
        pricingModelRepository.save(doc);
        return doc.getVersion();
    }

    private Object handleBudget(String orgId, String projId, String action, String id, Integer expectedVersion,
            java.util.Map<String, Object> payload) {
        if ("delete".equalsIgnoreCase(action)) {
            budgetRepository.findById(id).ifPresent(doc -> {
                validateOwnership(doc.getOrganizationId(), doc.getProjectId(), orgId, projId);
                budgetRepository.delete(doc);
            });
            return null;
        }

        FinBudget doc = "create".equalsIgnoreCase(action) ? new FinBudget()
                : budgetRepository.findById(id).orElseThrow(() -> new RuntimeException("Budget not found: " + id));

        if (!"create".equalsIgnoreCase(action)) {
            validateOwnership(doc.getOrganizationId(), doc.getProjectId(), orgId, projId);
            validateVersion(doc, expectedVersion);
        }

        applyPayload(doc, payload);
        validatorService.validate(doc);
        doc.setOrganizationId(orgId);
        doc.setProjectId(projId);
        if (doc.getCreatedAt() == null)
            doc.setCreatedAt(java.time.Instant.now());
        budgetRepository.save(doc);
        // If budget has monthly allocations, we could track months here too.
        // For now, budget changes trigger global recompute if formula is present,
        // or we could extract months from monthlyAllocations.
        return doc.getVersion();
    }

    private void handleScenario(String orgId, String projId, String action, String id, Integer expectedVersion,
            java.util.Map<String, Object> payload) {
        if ("delete".equalsIgnoreCase(action)) {
            scenarioRepository.findById(id).ifPresent(doc -> {
                validateOwnership(doc.getOrganizationId(), doc.getProjectId(), orgId, projId);
                scenarioRepository.delete(doc);
            });
            return;
        }

        FinScenario doc = "create".equalsIgnoreCase(action) ? new FinScenario()
                : scenarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Scenario not found: " + id));

        if (!"create".equalsIgnoreCase(action)) {
            validateOwnership(doc.getOrganizationId(), doc.getProjectId(), orgId, projId);
        }

        applyPayload(doc, payload);
        doc.setOrganizationId(orgId);
        doc.setProjectId(projId);
        if (doc.getCreatedAt() == null)
            doc.setCreatedAt(java.time.Instant.now());
        scenarioRepository.save(doc);
    }

    private void validateOwnership(String docOrgId, String docProjId, String reqOrgId, String reqProjId) {
        if (!reqOrgId.equals(docOrgId) || !reqProjId.equals(docProjId)) {
            log.error("Ownership violation: doc({},{}) req({},{})", docOrgId, docProjId, reqOrgId, reqProjId);
            throw new SecurityException("Tenant isolation violation: Unauthorized access to project data");
        }
    }

    private void validateVersion(VersionedEntity entity, Integer expectedVersion) {
        if (expectedVersion == null) {
            throw new IllegalArgumentException("Expected version is required for updates");
        }
        if (entity.getVersion() != null && !entity.getVersion().equals(expectedVersion)) {
            log.warn("Version conflict for {}: current={}, expected={}", entity.getClass().getSimpleName(),
                    entity.getVersion(), expectedVersion);
            throw new FinVersionConflictException("VERSION_CONFLICT");
        }
    }

    private Object getOldState(String organizationId, String projectId, BulkUpdateDto.BulkOperation op) {
        if ("create".equalsIgnoreCase(op.getAction())) return null;
        
        String id = op.getId();
        switch (op.getType().toLowerCase()) {
            case "assumption": return assumptionRepository.findById(id).orElse(null);
            case "sales": return salesPlanRepository.findById(id).orElse(null);
            case "pricing": return pricingModelRepository.findById(id).orElse(null);
            case "budget": return budgetRepository.findById(id).orElse(null);
            case "scenario": return scenarioRepository.findById(id).orElse(null);
            default: return null;
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
