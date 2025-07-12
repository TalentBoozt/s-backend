package com.talentboozt.s_backend.domains.ambassador.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Getter
@Setter

@Document(collection = "ambassador_profiles")
public class AmbassadorProfileModel {
    @Id
    private String id;

    // ✅ Application Info
    private String name;
    private String email;
    private String motivation;
    private String profileLink;
    private boolean consentGiven;
    private Instant appliedAt;
    private String applicationStatus; // REQUESTED, REVIEWING, ACCEPTED, REJECTED

    // ✅ Assigned after approval
    @Indexed(unique = true, sparse = true)
    private String employeeId; // Assigned when application is accepted

    private String level; // BRONZE, GOLD, PLATINUM

    private int totalReferrals;
    private int coursePurchasesByReferrals;

    private int hostedSessions; // Sessions they've hosted
    private int trainingSessionsAttended;

    private boolean active;
    private Instant joinedAt;
    private Instant lastActivity;

    private List<String> badges; // Optional - e.g., ["EARLY_BIRD", "TOP_HOST"]
    private String referralCode; // e.g., "john-doe123"

    private String status; // ACTIVE, SUSPENDED, REQUESTED, REJECTED, etc.
    private String interviewNote;
    private List<String> badgeHistory; // track earned dates
    private Map<String, Object> perks; // discountPercent, freeCourseCount, etc.

    private int points = 0;
    private Instant lastPointEarnedAt;
    private int totalLogins = 0;
    private int consecutiveLoginDays = 0;
    private Instant lastLoginDate;
}
