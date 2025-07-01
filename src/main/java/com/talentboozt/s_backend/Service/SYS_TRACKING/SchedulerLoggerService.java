package com.talentboozt.s_backend.Service.SYS_TRACKING;

import com.talentboozt.s_backend.Model.SYS_TRACKING.SchedulerLogModel;
import com.talentboozt.s_backend.Repository.SYS_TRACKING.SchedulerLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class SchedulerLoggerService {

    @Autowired
    private SchedulerLogRepository logRepo;

    public void log(String jobName, String status, String message) {
        SchedulerLogModel log = new SchedulerLogModel();
        log.setJobName(jobName);
        log.setRunAt(Instant.now());
        log.setStatus(status);
        log.setMessage(message != null ? message : "");
        log.setExpireAt(Instant.now().plus(30, ChronoUnit.DAYS));
        logRepo.save(log);
    }
}
