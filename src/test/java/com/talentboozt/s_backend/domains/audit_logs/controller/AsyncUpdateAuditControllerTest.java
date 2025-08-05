package com.talentboozt.s_backend.domains.audit_logs.controller;

import com.talentboozt.s_backend.domains.audit_logs.model.AsyncUpdateAuditLog;
import com.talentboozt.s_backend.domains.audit_logs.repository.AsyncUpdateAuditLogRepository;
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
class AsyncUpdateAuditControllerTest {

    @Mock
    private AsyncUpdateAuditLogRepository asyncUpdateAuditLogRepository;

    @InjectMocks
    private AsyncUpdateAuditController asyncUpdateAuditController;

    @Test
    void getAllLogsReturnsAllLogs() {
        List<AsyncUpdateAuditLog> logs = List.of(new AsyncUpdateAuditLog(), new AsyncUpdateAuditLog());
        when(asyncUpdateAuditLogRepository.findAll()).thenReturn(logs);

        Iterable<AsyncUpdateAuditLog> result = asyncUpdateAuditController.getAllLogs();

        assertEquals(logs, result);
        verify(asyncUpdateAuditLogRepository).findAll();
    }

    @Test
    void getLogCountReturnsCorrectCount() {
        when(asyncUpdateAuditLogRepository.count()).thenReturn(5L);

        long result = asyncUpdateAuditController.getLogCount();

        assertEquals(5L, result);
        verify(asyncUpdateAuditLogRepository).count();
    }

    @Test
    void getLatestLogReturnsMostRecentLog() {
        AsyncUpdateAuditLog latestLog = new AsyncUpdateAuditLog();
        when(asyncUpdateAuditLogRepository.findTopByOrderByCreatedAtDesc()).thenReturn(latestLog);

        AsyncUpdateAuditLog result = asyncUpdateAuditController.getLatestLog();

        assertEquals(latestLog, result);
        verify(asyncUpdateAuditLogRepository).findTopByOrderByCreatedAtDesc();
    }

    @Test
    void getPaginatedLogsReturnsPaginatedResults() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AsyncUpdateAuditLog> page = new PageImpl<>(List.of(new AsyncUpdateAuditLog(), new AsyncUpdateAuditLog()));
        when(asyncUpdateAuditLogRepository.searchWithFilter(null, pageable)).thenReturn(page);

        Map<String, Object> result = asyncUpdateAuditController.getPaginatedLogs(1, 10, null);

        assertEquals(2, ((List<?>) result.get("items")).size());
        assertEquals(2L, result.get("total"));
        verify(asyncUpdateAuditLogRepository).searchWithFilter(null, pageable);
    }

    @Test
    void getPaginatedLogsHandlesEmptyResults() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AsyncUpdateAuditLog> page = new PageImpl<>(List.of());
        when(asyncUpdateAuditLogRepository.searchWithFilter(null, pageable)).thenReturn(page);

        Map<String, Object> result = asyncUpdateAuditController.getPaginatedLogs(1, 10, null);

        assertTrue(((List<?>) result.get("items")).isEmpty());
        assertEquals(0L, result.get("total"));
        verify(asyncUpdateAuditLogRepository).searchWithFilter(null, pageable);
    }
}
