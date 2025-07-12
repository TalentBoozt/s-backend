package com.talentboozt.s_backend.domains.plat_courses.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter

@Document(collection = "ambassador_task_progress")
public class AmbassadorTaskProgressModel {
    @Id
    private String id;

    @Indexed
    private String ambassadorId;
    private String taskId;
    private String taskType;

    private int progressValue;
    private boolean completed;

    private Instant startedAt;
    private Instant completedAt;
    private Instant lastResetAt;

    private String rewardStatus = "NOT_ISSUED"; // or enum
    private boolean rewarded = false;
    private Instant rewardedAt;
}
