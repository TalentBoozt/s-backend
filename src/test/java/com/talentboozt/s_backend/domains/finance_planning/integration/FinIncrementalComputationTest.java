package com.talentboozt.s_backend.domains.finance_planning.integration;

import com.talentboozt.s_backend.domains.finance_planning.dtos.BulkUpdateDto;
import com.talentboozt.s_backend.domains.finance_planning.models.*;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.*;
import com.talentboozt.s_backend.domains.finance_planning.services.FinBulkUpdateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class FinIncrementalComputationTest {

    @Autowired
    private FinBulkUpdateService bulkUpdateService;

    @Autowired
    private FinSalesPlanRepository salesPlanRepository;

    @Autowired
    private FinFinancialSnapshotRepository snapshotRepository;

    @Autowired
    private FinPricingModelRepository pricingModelRepository;

    private final String orgId = "inc-org";
    private final String projId = "inc-proj";

    @BeforeEach
    void setup() {
        snapshotRepository.deleteAll();
        salesPlanRepository.deleteAll();
        pricingModelRepository.deleteAll();

        // Setup base pricing
        FinPricingModel pricing = new FinPricingModel();
        pricing.setOrganizationId(orgId);
        pricing.setProjectId(projId);
        pricing.setTier("pro");
        pricing.setPrice(100.0);
        pricing.setCostPerUser(20.0);
        pricingModelRepository.save(pricing);
    }

    @Test
    void shouldOnlyUpdateAffectedMonth() throws InterruptedException {
        // 1. Create two months of sales
        createSales("2024-01", 10);
        createSales("2024-02", 20);

        // Trigger initial compute
        bulkUpdateService.processBulkUpdate(orgId, projId, new BulkUpdateDto());

        FinFinancialSnapshot s1 = snapshotRepository.findByOrganizationIdAndProjectIdAndScenarioIdAndMonth(orgId, projId, "base", "2024-01").orElseThrow();
        FinFinancialSnapshot s2 = snapshotRepository.findByOrganizationIdAndProjectIdAndScenarioIdAndMonth(orgId, projId, "base", "2024-02").orElseThrow();
        
        Instant t1 = s1.getComputedAt();
        Instant t2 = s2.getComputedAt();

        Thread.sleep(100); // Ensure timestamp difference

        // 2. Update ONLY 2024-01
        FinSalesPlan plan1 = salesPlanRepository.findByOrganizationIdAndProjectId(orgId, projId).stream()
                .filter(p -> p.getMonth().equals("2024-01")).findFirst().orElseThrow();

        BulkUpdateDto.BulkOperation op = new BulkUpdateDto.BulkOperation();
        op.setType("sales");
        op.setAction("update");
        op.setId(plan1.getId());
        op.setExpectedVersion(plan1.getVersion());
        op.setPayload(Map.of("userCounts", Map.of("pro", 15)));

        BulkUpdateDto request = new BulkUpdateDto();
        request.setOperations(List.of(op));

        bulkUpdateService.processBulkUpdate(orgId, projId, request);

        // 3. Verify
        FinFinancialSnapshot u1 = snapshotRepository.findByOrganizationIdAndProjectIdAndScenarioIdAndMonth(orgId, projId, "base", "2024-01").orElseThrow();
        FinFinancialSnapshot u2 = snapshotRepository.findByOrganizationIdAndProjectIdAndScenarioIdAndMonth(orgId, projId, "base", "2024-02").orElseThrow();

        assertEquals(1500.0, u1.getRevenue(), "2024-01 revenue should be updated");
        assertTrue(u1.getComputedAt().isAfter(t1), "2024-01 should have new computation timestamp");
        
        assertEquals(t2, u2.getComputedAt(), "2024-02 should NOT have been recomputed");
    }

    private void createSales(String month, int count) {
        FinSalesPlan plan = new FinSalesPlan();
        plan.setOrganizationId(orgId);
        plan.setProjectId(projId);
        plan.setMonth(month);
        plan.setUserCounts(Map.of("pro", count));
        salesPlanRepository.save(plan);
    }
}
