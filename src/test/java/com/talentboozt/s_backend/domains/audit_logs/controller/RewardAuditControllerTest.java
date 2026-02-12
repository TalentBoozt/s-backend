package com.talentboozt.s_backend.domains.audit_logs.controller;

import com.talentboozt.s_backend.domains.audit_logs.model.TaskRewardAuditModel;
import com.talentboozt.s_backend.domains.audit_logs.repository.mongodb.TaskRewardAuditRepository;

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
class RewardAuditControllerTest {

    @Mock
    private TaskRewardAuditRepository auditRepo;

    @InjectMocks
    private RewardAuditController rewardAuditController;

    @Test
    void getByAmbassadorReturnsLogsForAmbassador() {
        List<TaskRewardAuditModel> logs = List.of(new TaskRewardAuditModel(), new TaskRewardAuditModel());
        when(auditRepo.findByAmbassadorIdOrderByIssuedAtDesc("ambassador123")).thenReturn(logs);

        List<TaskRewardAuditModel> result = rewardAuditController.getByAmbassador("ambassador123");

        assertEquals(logs, result);
        verify(auditRepo).findByAmbassadorIdOrderByIssuedAtDesc("ambassador123");
    }

    @Test
    void getByTaskReturnsLogsForTask() {
        List<TaskRewardAuditModel> logs = List.of(new TaskRewardAuditModel(), new TaskRewardAuditModel());
        when(auditRepo.findByTaskIdOrderByIssuedAtDesc("task456")).thenReturn(logs);

        List<TaskRewardAuditModel> result = rewardAuditController.getByTask("task456");

        assertEquals(logs, result);
        verify(auditRepo).findByTaskIdOrderByIssuedAtDesc("task456");
    }

    @Test
    void getRewardAuditsReturnsPaginatedResults() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "issuedAt"));
        Page<TaskRewardAuditModel> page = new PageImpl<>(List.of(new TaskRewardAuditModel()));
        when(auditRepo.search(null, null, pageable)).thenReturn(page);

        Map<String, Object> result = rewardAuditController.getRewardAudits(1, 10, null, null);

        assertEquals(2, ((List<?>) result.get("items")).size());
        assertEquals(2L, result.get("total"));
        verify(auditRepo).search(null, null, pageable);
    }

    @Test
    void getRewardAuditsHandlesEmptyResults() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "issuedAt"));
        Page<TaskRewardAuditModel> page = new PageImpl<>(List.of());
        when(auditRepo.search(null, null, pageable)).thenReturn(page);

        Map<String, Object> result = rewardAuditController.getRewardAudits(1, 10, null, null);

        assertTrue(((List<?>) result.get("items")).isEmpty());
        assertEquals(0L, result.get("total"));
        verify(auditRepo).search(null, null, pageable);
    }

    @Test
    void getRewardAuditsAppliesFiltersCorrectly() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "issuedAt"));
        Page<TaskRewardAuditModel> page = new PageImpl<>(List.of(new TaskRewardAuditModel()));
        when(auditRepo.search("ambassador123", "COMPLETED", pageable)).thenReturn(page);

        Map<String, Object> result = rewardAuditController.getRewardAudits(1, 10, "ambassador123", "COMPLETED");

        assertEquals(1, ((List<?>) result.get("items")).size());
        assertEquals(1L, result.get("total"));
        verify(auditRepo).search("ambassador123", "COMPLETED", pageable);
    }

    @Test
    void getRewardAuditsHandlesInvalidPageAndSizeGracefully() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "issuedAt"));
        Page<TaskRewardAuditModel> page = new PageImpl<>(List.of());
        when(auditRepo.search(null, null, pageable)).thenReturn(page);

        Map<String, Object> result = rewardAuditController.getRewardAudits(-1, 0, null, null);

        assertTrue(((List<?>) result.get("items")).isEmpty());
        assertEquals(0L, result.get("total"));
        verify(auditRepo).search(null, null, pageable);
    }
}
