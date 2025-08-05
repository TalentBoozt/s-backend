package com.talentboozt.s_backend.domains.audit_logs.service;

import com.talentboozt.s_backend.domains.audit_logs.repository.ClientActAuditLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientActAuditLogServiceTest {

    @Mock
    private ClientActAuditLogRepository auditLogRepository;

    @InjectMocks
    private ClientActAuditLogService clientActAuditLogService;

    @Test
    void logAddsEntryToQueue() {
        Map<String, Object> details = Map.of("key", "value");
        clientActAuditLogService.log("user123", "192.168.1.1", "session123", "LOGIN", "IpCaptureFilter", details);

        assertEquals(1, clientActAuditLogService.auditQueue.size());
    }

    @Test
    void logDropsEntryWhenQueueIsFull() {
        for (int i = 0; i < 10_000; i++) {
            clientActAuditLogService.log("user" + i, "192.168.1.1", "session" + i, "ACTION", "source", Map.of());
        }

        clientActAuditLogService.log("userOverflow", "192.168.1.1", "sessionOverflow", "OVERFLOW", "source", Map.of());

        assertEquals(10_000, clientActAuditLogService.auditQueue.size());
    }

    @Test
    void flushLogsSavesBatchToRepository() {
        for (int i = 0; i < 50; i++) {
            clientActAuditLogService.log("user" + i, "192.168.1.1", "session" + i, "ACTION", "source", Map.of());
        }

        clientActAuditLogService.flushLogs();

        verify(auditLogRepository).saveAll(anyList());
        assertEquals(0, clientActAuditLogService.auditQueue.size());
    }

    @Test
    void flushLogsHandlesEmptyQueueGracefully() {
        clientActAuditLogService.flushLogs();

        verify(auditLogRepository, never()).saveAll(anyList());
    }

    @Test
    void shutdownStopsScheduler() {
        clientActAuditLogService.init();
        clientActAuditLogService.shutdown();

        assertTrue(clientActAuditLogService.scheduler.isShutdown());
    }
}
