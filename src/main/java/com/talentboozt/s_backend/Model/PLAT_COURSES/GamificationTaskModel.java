package com.talentboozt.s_backend.Model.PLAT_COURSES;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Setter
@Getter

@Document(collection = "gamification_tasks")
public class GamificationTaskModel {
    @Id
    private String id;

    private String title;              // "Refer 5 Users", "Host a Session"
    private String description;

    private String type;              // REFERRAL, SESSION_HOSTING, TRAINING_ATTENDANCE
    private String level;             // BRONZE, GOLD, PLATINUM, or null = all

    private int targetValue;          // e.g., 5 referrals
    private String rewardType;        // COUPON, VOUCHER, BADGE, SWAG
    private String rewardId;          // Optional (e.g., couponId or rewardConfigId)

    private boolean recurring;        // true = can repeat monthly etc.
    private int frequencyInDays;      // if recurring, cooldown period

    private Instant createdAt;
}
