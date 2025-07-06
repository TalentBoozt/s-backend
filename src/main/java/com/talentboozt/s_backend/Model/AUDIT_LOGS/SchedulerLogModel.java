package com.talentboozt.s_backend.Model.AUDIT_LOGS;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter

@Document("scheduler_logs")
public class SchedulerLogModel {
    @Id
    private String id;

    private String jobName;      // e.g., updateTaskProgress, resetRecurringTasks
    private Instant runAt;       // When it ran
    private String status;       // SUCCESS / ERROR
    private String message;      // Optional: summary or exception message
    @Indexed(name = "expireAtIndex", expireAfter = "0s")
    private Instant expireAt; // Mongo will delete after this time
}
