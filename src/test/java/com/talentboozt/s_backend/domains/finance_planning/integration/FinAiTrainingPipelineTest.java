package com.talentboozt.s_backend.domains.finance_planning.integration;

import com.talentboozt.s_backend.domains.finance_planning.dtos.BulkUpdateDto;
import com.talentboozt.s_backend.domains.finance_planning.models.*;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.*;
import com.talentboozt.s_backend.domains.finance_planning.services.FinBulkUpdateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class FinAiTrainingPipelineTest {

    @Autowired
    private FinBulkUpdateService bulkUpdateService;

    @Autowired
    private FinAiTrainingSnapshotRepository aiTrainingSnapshotRepository;

    @Autowired
    private FinFinancialSnapshotRepository financialSnapshotRepository;

    @Autowired
    private FinSalesPlanRepository salesPlanRepository;

    @Autowired
    private FinPricingModelRepository pricingModelRepository;

    private final String orgId = "ai-org";
    private final String projId = "ai-proj";

    @BeforeEach
    void setup() {
        aiTrainingSnapshotRepository.deleteAll();
        financialSnapshotRepository.deleteAll();
        salesPlanRepository.deleteAll();
        pricingModelRepository.deleteAll();
        
        // Setup initial pricing
        FinPricingModel pricing = new FinPricingModel();
        pricing.setOrganizationId(orgId);
        pricing.setProjectId(projId);
        pricing.setTier("PRO");
        pricing.setPrice(100.0);
        pricingModelRepository.save(pricing);
    }

    @Test
    @WithMockUser(username = "ai-user")
    void shouldCaptureSnapshotAfterUpdate() throws InterruptedException {
        BulkUpdateDto request = new BulkUpdateDto();
        BulkUpdateDto.BulkOperation op = new BulkUpdateDto.BulkOperation();
        op.setType("sales");
        op.setAction("create");
        op.setPayload(Map.of("month", "2024-05", "userCounts", Map.of("PRO", 50)));
        request.setOperations(List.of(op));

        bulkUpdateService.processBulkUpdate(orgId, projId, request);

        // Wait for async listener to process
        Thread.sleep(1000);

        List<FinAiTrainingSnapshot> snapshots = aiTrainingSnapshotRepository.findAll();
        assertFalse(snapshots.isEmpty(), "Snapshot should be created");
        
        FinAiTrainingSnapshot snapshot = snapshots.get(0);
        assertEquals(orgId, snapshot.getOrganizationId());
        assertEquals(projId, snapshot.getProjectId());
        assertNotNull(snapshot.getInputSnapshot(), "Input JSON should not be null");
        assertNotNull(snapshot.getOutputSnapshot(), "Output JSON should not be null");
        assertTrue(snapshot.getChangedFields().contains("sales.create"), "Should track changed fields");
        
        // Verify output content contains revenue
        assertTrue(snapshot.getOutputSnapshot().contains("5000.0"), "Output should contain computed revenue (50 * 100)");
    }
}
