package com.talentboozt.s_backend.Model.PLAT_COURSES;

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

    private int progressValue;
    private boolean completed;

    private Instant startedAt;
    private Instant completedAt;

    private String rewardStatus; // NOT_ISSUED, ISSUED, REDEEMED
}
