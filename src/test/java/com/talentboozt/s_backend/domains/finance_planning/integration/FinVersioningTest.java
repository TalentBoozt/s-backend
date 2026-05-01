package com.talentboozt.s_backend.domains.finance_planning.integration;

import com.talentboozt.s_backend.domains.finance_planning.dtos.BulkUpdateDto;
import com.talentboozt.s_backend.domains.finance_planning.exception.FinVersionConflictException;
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
public class FinVersioningTest {

    @Autowired
    private FinBulkUpdateService bulkUpdateService;

    @Autowired
    private FinAssumptionRepository assumptionRepository;

    private final String orgId = "test-org";
    private final String projId = "test-proj";

    @BeforeEach
    void setup() {
        assumptionRepository.deleteAll();
    }

    @Test
    void shouldRejectUpdateWithWrongVersion() {
        // 1. Create initial doc
        FinAssumption assumption = new FinAssumption();
        assumption.setOrganizationId(orgId);
        assumption.setProjectId(projId);
        assumption.setKey("rate");
        assumption.setValue("1.0");
        assumption = assumptionRepository.save(assumption);
        
        Integer originalVersion = assumption.getVersion();
        assertNotNull(originalVersion);

        // 2. Attempt update with WRONG version
        BulkUpdateDto.BulkOperation op = new BulkUpdateDto.BulkOperation();
        op.setType("assumption");
        op.setAction("update");
        op.setId(assumption.getId());
        op.setExpectedVersion(originalVersion - 1); // WRONG VERSION
        op.setPayload(Map.of("value", "2.0"));

        BulkUpdateDto request = new BulkUpdateDto();
        request.setOperations(List.of(op));

        assertThrows(FinVersionConflictException.class, () -> {
            bulkUpdateService.processBulkUpdate(orgId, projId, request);
        });

        // Verify value was NOT updated
        FinAssumption updated = assumptionRepository.findById(assumption.getId()).orElseThrow();
        assertEquals("1.0", updated.getValue());
    }

    @Test
    void shouldAcceptUpdateWithCorrectVersionAndIncrement() {
        // 1. Create initial doc
        FinAssumption assumption = new FinAssumption();
        assumption.setOrganizationId(orgId);
        assumption.setProjectId(projId);
        assumption.setKey("rate");
        assumption.setValue("1.0");
        assumption = assumptionRepository.save(assumption);
        
        Integer originalVersion = assumption.getVersion();

        // 2. Attempt update with CORRECT version
        BulkUpdateDto.BulkOperation op = new BulkUpdateDto.BulkOperation();
        op.setType("assumption");
        op.setAction("update");
        op.setId(assumption.getId());
        op.setExpectedVersion(originalVersion);
        op.setPayload(Map.of("value", "2.0"));

        BulkUpdateDto request = new BulkUpdateDto();
        request.setOperations(List.of(op));

        bulkUpdateService.processBulkUpdate(orgId, projId, request);

        // 3. Verify update and version increment
        FinAssumption updated = assumptionRepository.findById(assumption.getId()).orElseThrow();
        assertEquals("2.0", updated.getValue());
        assertTrue(updated.getVersion() > originalVersion);
    }
}
