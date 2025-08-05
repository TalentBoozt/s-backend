package com.talentboozt.s_backend.domains.audit_logs.controller;

import com.talentboozt.s_backend.domains.audit_logs.model.StripeAuditLog;
import com.talentboozt.s_backend.domains.audit_logs.repository.StripeAuditLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StripeAuditLogControllerTest {

    @Mock
    private StripeAuditLogRepository stripeAuditLogRepository;

    @InjectMocks
    private StripeAuditLogController stripeAuditLogController;

    @Test
    void getAllLogsReturnsAllLogs() {
        List<StripeAuditLog> logs = List.of(new StripeAuditLog(), new StripeAuditLog());
        when(stripeAuditLogRepository.findAll()).thenReturn(logs);

        List<StripeAuditLog> result = stripeAuditLogController.getAllLogs();

        assertEquals(logs, result);
        verify(stripeAuditLogRepository).findAll();
    }

    @Test
    void getLogsForRetryReturnsLogsWithRetryPendingStatus() {
        List<StripeAuditLog> logs = List.of(new StripeAuditLog(), new StripeAuditLog());
        when(stripeAuditLogRepository.findTop20ByStatusAndRetryCountLessThanOrderByCreatedAtAsc("retry_pending", 3)).thenReturn(logs);

        List<StripeAuditLog> result = stripeAuditLogController.getLogsForRetry();

        assertEquals(logs, result);
        verify(stripeAuditLogRepository).findTop20ByStatusAndRetryCountLessThanOrderByCreatedAtAsc("retry_pending", 3);
    }

    @Test
    void getStripeLogsReturnsPaginatedResults() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<StripeAuditLog> page = new PageImpl<>(List.of(new StripeAuditLog(), new StripeAuditLog()));
        when(stripeAuditLogRepository.search(null, null, pageable)).thenReturn(page);

        Map<String, Object> result = stripeAuditLogController.getStripeLogs(1, 10, null, null);

        assertEquals(2, ((List<?>) result.get("items")).size());
        assertEquals(2L, result.get("total"));
        verify(stripeAuditLogRepository).search(null, null, pageable);
    }

    @Test
    void getStripeLogsHandlesEmptyResults() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<StripeAuditLog> page = new PageImpl<>(List.of());
        when(stripeAuditLogRepository.search(null, null, pageable)).thenReturn(page);

        Map<String, Object> result = stripeAuditLogController.getStripeLogs(1, 10, null, null);

        assertTrue(((List<?>) result.get("items")).isEmpty());
        assertEquals(0L, result.get("total"));
        verify(stripeAuditLogRepository).search(null, null, pageable);
    }

    @Test
    void getStripeLogsAppliesFiltersCorrectly() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<StripeAuditLog> page = new PageImpl<>(List.of(new StripeAuditLog()));
        when(stripeAuditLogRepository.search("eventType123", "SUCCESS", pageable)).thenReturn(page);

        Map<String, Object> result = stripeAuditLogController.getStripeLogs(1, 10, "eventType123", "SUCCESS");

        assertEquals(1, ((List<?>) result.get("items")).size());
        assertEquals(1L, result.get("total"));
        verify(stripeAuditLogRepository).search("eventType123", "SUCCESS", pageable);
    }

    @Test
    void getStripeLogsHandlesInvalidPageAndSizeGracefully() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<StripeAuditLog> page = new PageImpl<>(List.of());
        when(stripeAuditLogRepository.search(null, null, pageable)).thenReturn(page);

        Map<String, Object> result = stripeAuditLogController.getStripeLogs(-1, 0, null, null);

        assertTrue(((List<?>) result.get("items")).isEmpty());
        assertEquals(0L, result.get("total"));
        verify(stripeAuditLogRepository).search(null, null, pageable);
    }
}
