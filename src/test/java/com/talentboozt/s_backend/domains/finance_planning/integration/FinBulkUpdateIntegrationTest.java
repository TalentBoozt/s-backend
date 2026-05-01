package com.talentboozt.s_backend.domains.finance_planning.integration;

import com.talentboozt.s_backend.domains.finance_planning.dtos.BulkUpdateDto;
import com.talentboozt.s_backend.domains.finance_planning.models.FinAssumption;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinAssumptionRepository;
import com.talentboozt.s_backend.domains.finance_planning.services.FinBulkUpdateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class FinBulkUpdateIntegrationTest {

    @Autowired
    private FinBulkUpdateService bulkUpdateService;

    @Autowired
    private FinAssumptionRepository assumptionRepository;

    private final String orgId = "integration-org";
    private final String projId = "integration-proj";

    @BeforeEach
    void cleanUp() {
        assumptionRepository.deleteAll();
    }

    @Test
    void testBulkUpdateCycle() {
        // 1. Create an assumption
        BulkUpdateDto createRequest = new BulkUpdateDto();
        BulkUpdateDto.BulkOperation op1 = new BulkUpdateDto.BulkOperation();
        op1.setType("assumption");
        op1.setAction("create");
        op1.setId("temp-id-1");
        op1.setPayload(Map.of("key", "revenue_growth", "value", "15%"));
        createRequest.setOperations(List.of(op1));

        bulkUpdateService.processBulkUpdate(orgId, projId, createRequest);

        List<FinAssumption> assumptions = assumptionRepository.findByOrganizationIdAndProjectId(orgId, projId);
        assertEquals(1, assumptions.size());
        assertEquals("revenue_growth", assumptions.get(0).getKey());

        String realId = assumptions.get(0).getId();

        // 2. Update it
        BulkUpdateDto updateRequest = new BulkUpdateDto();
        BulkUpdateDto.BulkOperation op2 = new BulkUpdateDto.BulkOperation();
        op2.setType("assumption");
        op2.setAction("update");
        op2.setId(realId);
        op2.setPayload(Map.of("value", "20%"));
        updateRequest.setOperations(List.of(op2));

        bulkUpdateService.processBulkUpdate(orgId, projId, updateRequest);

        FinAssumption updated = assumptionRepository.findById(realId).orElseThrow();
        assertEquals("20%", updated.getValue());

        // 3. Delete it
        BulkUpdateDto deleteRequest = new BulkUpdateDto();
        BulkUpdateDto.BulkOperation op3 = new BulkUpdateDto.BulkOperation();
        op3.setType("assumption");
        op3.setAction("delete");
        op3.setId(realId);
        deleteRequest.setOperations(List.of(op3));

        bulkUpdateService.processBulkUpdate(orgId, projId, deleteRequest);

        assertFalse(assumptionRepository.existsById(realId));
    }
}
