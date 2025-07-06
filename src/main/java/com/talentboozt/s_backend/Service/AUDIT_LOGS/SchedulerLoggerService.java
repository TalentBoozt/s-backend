package com.talentboozt.s_backend.Service.AUDIT_LOGS;

import com.talentboozt.s_backend.Model.AUDIT_LOGS.SchedulerLogModel;
import com.talentboozt.s_backend.Repository.AUDIT_LOGS.SchedulerLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class SchedulerLoggerService {

    @Autowired
    private SchedulerLogRepository logRepo;

    @Value("${audit.expire-after-days:30}")
    private long expireAfterDays;

    public void log(String jobName, String status, String message) {
        SchedulerLogModel log = new SchedulerLogModel();
        log.setJobName(jobName);
        log.setRunAt(Instant.now());
        log.setStatus(status);
        log.setMessage(message != null ? message : "");
        log.setExpireAt(Instant.now().plus(expireAfterDays, ChronoUnit.DAYS));
        logRepo.save(log);
    }
}
