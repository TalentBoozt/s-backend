package com.talentboozt.s_backend.domains.audit_logs.controller;

import com.talentboozt.s_backend.domains.audit_logs.model.ClientActAuditLog;
import com.talentboozt.s_backend.domains.audit_logs.repository.ClientActAuditLogRepository;
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
class ClientActAuditLogControllerTest {

    @Mock
    private ClientActAuditLogRepository clientActAuditLogRepository;

    @InjectMocks
    private ClientActAuditLogController clientActAuditLogController;

    @Test
    void getAllLogsReturnsAllLogs() {
        List<ClientActAuditLog> logs = List.of(new ClientActAuditLog(), new ClientActAuditLog());
        when(clientActAuditLogRepository.findAll()).thenReturn(logs);

        Iterable<ClientActAuditLog> result = clientActAuditLogController.getAllLogs();

        assertEquals(logs, result);
        verify(clientActAuditLogRepository).findAll();
    }

    @Test
    void getClientLogsReturnsPaginatedResults() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<ClientActAuditLog> page = new PageImpl<>(List.of(new ClientActAuditLog(), new ClientActAuditLog()));
        when(clientActAuditLogRepository.searchWithFilter(null, pageable)).thenReturn(page);

        Map<String, Object> result = clientActAuditLogController.getClientLogs(1, 10, null);

        assertEquals(2, ((List<?>) result.get("items")).size());
        assertEquals(2L, result.get("total"));
        verify(clientActAuditLogRepository).searchWithFilter(null, pageable);
    }

    @Test
    void getClientLogsHandlesEmptyResults() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<ClientActAuditLog> page = new PageImpl<>(List.of());
        when(clientActAuditLogRepository.searchWithFilter(null, pageable)).thenReturn(page);

        Map<String, Object> result = clientActAuditLogController.getClientLogs(1, 10, null);

        assertTrue(((List<?>) result.get("items")).isEmpty());
        assertEquals(0L, result.get("total"));
        verify(clientActAuditLogRepository).searchWithFilter(null, pageable);
    }

    @Test
    void getClientLogsAppliesFilterCorrectly() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<ClientActAuditLog> page = new PageImpl<>(List.of(new ClientActAuditLog()));
        when(clientActAuditLogRepository.searchWithFilter("filterValue", pageable)).thenReturn(page);

        Map<String, Object> result = clientActAuditLogController.getClientLogs(1, 10, "filterValue");

        assertEquals(1, ((List<?>) result.get("items")).size());
        assertEquals(1L, result.get("total"));
        verify(clientActAuditLogRepository).searchWithFilter("filterValue", pageable);
    }

    @Test
    void getClientLogsHandlesInvalidPageAndSizeGracefully() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<ClientActAuditLog> page = new PageImpl<>(List.of());
        when(clientActAuditLogRepository.searchWithFilter(null, pageable)).thenReturn(page);

        Map<String, Object> result = clientActAuditLogController.getClientLogs(-1, 0, null);

        assertTrue(((List<?>) result.get("items")).isEmpty());
        assertEquals(0L, result.get("total"));
        verify(clientActAuditLogRepository).searchWithFilter(null, pageable);
    }
}
