package com.talentboozt.s_backend.domains.finance_planning.integration;

import com.talentboozt.s_backend.domains.finance_planning.dtos.BulkUpdateDto;
import com.talentboozt.s_backend.domains.finance_planning.exception.FinValidationException;
import com.talentboozt.s_backend.domains.finance_planning.models.FinPricingModel;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinPricingModelRepository;
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
public class FinValidationTest {

    @Autowired
    private FinBulkUpdateService bulkUpdateService;

    @Autowired
    private FinPricingModelRepository pricingModelRepository;

    private final String orgId = "val-org";
    private final String projId = "val-proj";

    @BeforeEach
    void setup() {
        pricingModelRepository.deleteAll();
    }

    @Test
    void shouldRejectNegativePrice() {
        BulkUpdateDto.BulkOperation op = new BulkUpdateDto.BulkOperation();
        op.setType("pricing");
        op.setAction("create");
        op.setPayload(Map.of(
                "tier", "pro",
                "price", -10.0,
                "costPerUser", 5.0
        ));

        BulkUpdateDto request = new BulkUpdateDto();
        request.setOperations(List.of(op));

        assertThrows(FinValidationException.class, () -> {
            bulkUpdateService.processBulkUpdate(orgId, projId, request);
        });
    }

    @Test
    void shouldRejectInvalidMonthFormat() {
        BulkUpdateDto.BulkOperation op = new BulkUpdateDto.BulkOperation();
        op.setType("sales");
        op.setAction("create");
        op.setPayload(Map.of(
                "month", "2024-13", // Invalid month
                "userCounts", Map.of("pro", 10)
        ));

        BulkUpdateDto request = new BulkUpdateDto();
        request.setOperations(List.of(op));

        assertThrows(FinValidationException.class, () -> {
            bulkUpdateService.processBulkUpdate(orgId, projId, request);
        });
    }
}
