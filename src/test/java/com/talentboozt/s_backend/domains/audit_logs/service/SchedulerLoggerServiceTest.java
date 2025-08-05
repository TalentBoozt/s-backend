package com.talentboozt.s_backend.domains.audit_logs.service;

import com.talentboozt.s_backend.domains.audit_logs.model.SchedulerLogModel;
import com.talentboozt.s_backend.domains.audit_logs.repository.SchedulerLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchedulerLoggerServiceTest {

    @Mock
    private SchedulerLogRepository logRepo;

    @InjectMocks
    private SchedulerLoggerService schedulerLoggerService;

    @Test
    void logSavesLogWithValidData() {
        SchedulerLogModel log = new SchedulerLogModel();
        log.setJobName("updateTaskProgress");
        log.setRunAt(Instant.now().plusSeconds(10)); // Simulating a future run time
        log.setStatus("SUCCESS");
        log.setMessage("Job completed successfully");
        log.setExpireAt(Instant.now().plus(31, ChronoUnit.DAYS));
        when(logRepo.save(any(SchedulerLogModel.class))).thenReturn(log);

        schedulerLoggerService.log("updateTaskProgress", "SUCCESS", "Job completed successfully");

        verify(logRepo).save(argThat(savedLog ->
                savedLog.getJobName().equals("updateTaskProgress") &&
                        savedLog.getRunAt().isBefore(Instant.now().plusSeconds(10)) &&
                        savedLog.getStatus().equals("SUCCESS") &&
                        savedLog.getMessage().equals("Job completed successfully") &&
                        savedLog.getExpireAt().isBefore(Instant.now().plus(31, ChronoUnit.DAYS))
        ));
    }

    @Test
    void logHandlesNullMessageGracefully() {
        schedulerLoggerService.log("resetRecurringTasks", "ERROR", null);

        verify(logRepo).save(argThat(log ->
                log.getJobName().equals("resetRecurringTasks") &&
                        log.getStatus().equals("ERROR") &&
                        log.getMessage().isEmpty()
        ));
    }

    @Test
    void logSetsExpirationTimeBasedOnConfiguredDays() {
        SchedulerLogModel log = new SchedulerLogModel();
        log.setJobName("dailyJob");
        log.setRunAt(Instant.now().plusSeconds(10)); // Simulating a future run time, 10 seconds from now
        log.setStatus("SUCCESS");
        log.setMessage("Job ran successfully");
        log.setExpireAt(Instant.now().plus(31, ChronoUnit.DAYS)); // Expiration time set to 31 days from the current time
        when(logRepo.save(any(SchedulerLogModel.class))).thenReturn(log);

        schedulerLoggerService.log("dailyJob", "SUCCESS", "Job ran successfully");

        // Tolerance for comparison: Allow the expiration time to be within 1 second of the expected expiration time
        Instant expectedExpireAt = Instant.now().plus(31, ChronoUnit.DAYS);

        verify(logRepo).save(argThat(savedLog ->
                savedLog.getJobName().equals("dailyJob") &&
                        savedLog.getRunAt().isAfter(Instant.now().minusSeconds(1)) &&
                        savedLog.getRunAt().isBefore(Instant.now().plusSeconds(10)) &&

                        savedLog.getStatus().equals("SUCCESS") &&
                        savedLog.getMessage().equals("Job ran successfully") &&

                        savedLog.getExpireAt().isBefore(expectedExpireAt.plusSeconds(2)) &&
                        savedLog.getExpireAt().isAfter(expectedExpireAt.minus(32, ChronoUnit.DAYS))
        ));
    }
}
