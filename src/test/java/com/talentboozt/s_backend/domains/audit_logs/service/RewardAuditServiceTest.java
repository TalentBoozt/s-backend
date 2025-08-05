package com.talentboozt.s_backend.domains.audit_logs.service;

import com.talentboozt.s_backend.domains.ambassador.model.AmbassadorProfileModel;
import com.talentboozt.s_backend.domains.audit_logs.repository.TaskRewardAuditRepository;
import com.talentboozt.s_backend.domains.plat_courses.model.GamificationTaskModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RewardAuditServiceTest {

    @Mock
    private TaskRewardAuditRepository auditRepo;

    @InjectMocks
    private RewardAuditService rewardAuditService;

    @Test
    void recordSavesAuditWithValidData() {
        AmbassadorProfileModel ambassador = new AmbassadorProfileModel();
        ambassador.setId("ambassador123");
        GamificationTaskModel task = new GamificationTaskModel();
        task.setId("task456");

        rewardAuditService.record(ambassador, task, "COUPON", "reward789", "10% Discount", "ISSUED", "Reward issued successfully");

        verify(auditRepo).save(argThat(audit ->
                audit.getAmbassadorId().equals("ambassador123") &&
                        audit.getTaskId().equals("task456") &&
                        audit.getRewardType().equals("COUPON") &&
                        audit.getRewardId().equals("reward789") &&
                        audit.getRewardTitle().equals("10% Discount") &&
                        audit.getStatus().equals("ISSUED") &&
                        audit.getNote().equals("Reward issued successfully") &&
                        audit.getExpiresAt().isAfter(Instant.now())
        ));
    }

    @Test
    void recordHandlesNullRewardTitleGracefully() {
        AmbassadorProfileModel ambassador = new AmbassadorProfileModel();
        ambassador.setId("ambassador123");
        GamificationTaskModel task = new GamificationTaskModel();
        task.setId("task456");

        rewardAuditService.record(ambassador, task, "BADGE", "reward789", null, "FAILED", "Reward title is missing");

        verify(auditRepo).save(argThat(audit ->
                audit.getRewardTitle() == null &&
                        audit.getStatus().equals("FAILED") &&
                        audit.getNote().equals("Reward title is missing")
        ));
    }

    @Test
    void recordSetsExpirationTimeToSevenDays() {
        AmbassadorProfileModel ambassador = new AmbassadorProfileModel();
        ambassador.setId("ambassador123");
        GamificationTaskModel task = new GamificationTaskModel();
        task.setId("task456");

        rewardAuditService.record(ambassador, task, "SWAG", "reward789", "T-Shirt", "ISSUED", "Reward issued successfully");

        verify(auditRepo).save(argThat(audit ->
                audit.getExpiresAt().isAfter(Instant.now()) &&
                        audit.getExpiresAt().isBefore(Instant.now().plusSeconds(60 * 60 * 24 * 7 + 1))
        ));
    }

    @Test
    void recordHandlesNullAmbassadorIdGracefully() {
        GamificationTaskModel task = new GamificationTaskModel();
        AmbassadorProfileModel ambassador = new AmbassadorProfileModel();
        task.setId("task456");

        rewardAuditService.record(ambassador, task, "COUPON", "reward789", "10% Discount", "FAILED", "Ambassador ID is missing");

        verify(auditRepo).save(argThat(audit ->
                audit.getAmbassadorId() == null &&
                        audit.getStatus().equals("FAILED") &&
                        audit.getNote().equals("Ambassador ID is missing")
        ));
    }

    @Test
    void recordHandlesNullTaskIdGracefully() {
        AmbassadorProfileModel ambassador = new AmbassadorProfileModel();
        GamificationTaskModel task = new GamificationTaskModel();
        ambassador.setId("ambassador123");

        rewardAuditService.record(ambassador, task, "COUPON", "reward789", "10% Discount", "FAILED", "Task ID is missing");

        verify(auditRepo).save(argThat(audit ->
                audit.getTaskId() == null &&
                        audit.getStatus().equals("FAILED") &&
                        audit.getNote().equals("Task ID is missing")
        ));
    }
}
