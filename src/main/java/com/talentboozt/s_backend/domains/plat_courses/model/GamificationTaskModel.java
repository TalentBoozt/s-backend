package com.talentboozt.s_backend.domains.plat_courses.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

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
    private Map<String, Object> rewardMetadata; // Optional

    private boolean recurring;        // true = can repeat monthly etc.
    private int frequencyInDays;      // if recurring, cooldown period

    private Instant createdAt;

    private int priority = 0;     // 0 = lowest, higher = more important
    private String groupKey;      // Optional: e.g., "JULY_PROMO" or "LEVEL1"
    private int points = 0;       // points count for completing task
}
