package com.talentboozt.s_backend.domains.audit_logs.controller;

import com.talentboozt.s_backend.domains.audit_logs.model.SchedulerLogModel;
import com.talentboozt.s_backend.domains.audit_logs.repository.SchedulerLogRepository;
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
class SchedulerLogControllerTest {

    @Mock
    private SchedulerLogRepository repo;

    @InjectMocks
    private SchedulerLogController schedulerLogController;

    @Test
    void getLogsReturnsLogsForJobName() {
        List<SchedulerLogModel> logs = List.of(new SchedulerLogModel(), new SchedulerLogModel());
        when(repo.findByJobNameOrderByRunAtDesc("jobName123")).thenReturn(logs);

        List<SchedulerLogModel> result = schedulerLogController.getLogs("jobName123");

        assertEquals(logs, result);
        verify(repo).findByJobNameOrderByRunAtDesc("jobName123");
    }

    @Test
    void getAllLogsReturnsAllLogs() {
        List<SchedulerLogModel> logs = List.of(new SchedulerLogModel(), new SchedulerLogModel());
        when(repo.findAll()).thenReturn(logs);

        List<SchedulerLogModel> result = schedulerLogController.getAllLogs();

        assertEquals(logs, result);
        verify(repo).findAll();
    }

    @Test
    void getSchedulerLogsReturnsPaginatedResults() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "runAt"));
        Page<SchedulerLogModel> page = new PageImpl<>(List.of(new SchedulerLogModel(), new SchedulerLogModel()));
        when(repo.search(null, null, pageable)).thenReturn(page);

        Map<String, Object> result = schedulerLogController.getSchedulerLogs(1, 10, null, null);

        assertEquals(2, ((List<?>) result.get("items")).size());
        assertEquals(2L, result.get("total"));
        verify(repo).search(null, null, pageable);
    }

    @Test
    void getSchedulerLogsHandlesEmptyResults() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "runAt"));
        Page<SchedulerLogModel> page = new PageImpl<>(List.of());
        when(repo.search(null, null, pageable)).thenReturn(page);

        Map<String, Object> result = schedulerLogController.getSchedulerLogs(1, 10, null, null);

        assertTrue(((List<?>) result.get("items")).isEmpty());
        assertEquals(0L, result.get("total"));
        verify(repo).search(null, null, pageable);
    }

    @Test
    void getSchedulerLogsAppliesFiltersCorrectly() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "runAt"));
        Page<SchedulerLogModel> page = new PageImpl<>(List.of(new SchedulerLogModel()));
        when(repo.search("jobName123", "SUCCESS", pageable)).thenReturn(page);

        Map<String, Object> result = schedulerLogController.getSchedulerLogs(1, 10, "jobName123", "SUCCESS");

        assertEquals(1, ((List<?>) result.get("items")).size());
        assertEquals(1L, result.get("total"));
        verify(repo).search("jobName123", "SUCCESS", pageable);
    }

    @Test
    void getSchedulerLogsHandlesInvalidPageAndSizeGracefully() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "runAt"));
        Page<SchedulerLogModel> page = new PageImpl<>(List.of());
        when(repo.search(null, null, pageable)).thenReturn(page);

        Map<String, Object> result = schedulerLogController.getSchedulerLogs(-1, 0, null, null);

        assertTrue(((List<?>) result.get("items")).isEmpty());
        assertEquals(0L, result.get("total"));
        verify(repo).search(null, null, pageable);
    }
}
