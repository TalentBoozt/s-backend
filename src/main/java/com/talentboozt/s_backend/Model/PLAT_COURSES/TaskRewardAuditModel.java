package com.talentboozt.s_backend.Model.PLAT_COURSES;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter

@Document("task_reward_audit")
public class TaskRewardAuditModel {
    @Id
    private String id;

    private String ambassadorId;
    private String taskId;

    private String rewardType;     // e.g., "COUPON", "BADGE", "SWAG"
    private String rewardId;       // ID of coupon or reward model (if applicable)
    private String rewardTitle;    // Optional human-readable title or badge name

    private String status;         // e.g., "ISSUED", "SKIPPED", "FAILED"
    private String note;           // Optional debug message or context

    private Instant issuedAt;
}
