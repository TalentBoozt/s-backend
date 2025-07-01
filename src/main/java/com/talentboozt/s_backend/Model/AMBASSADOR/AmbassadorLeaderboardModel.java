package com.talentboozt.s_backend.Model.AMBASSADOR;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Setter
@Getter

@Document(collection = "ambassador_leaderboard")
public class AmbassadorLeaderboardModel {

    @Id
    private String id;

    private String type; // e.g., "REFERRAL", "SESSION_HOSTING", etc.

    private String ambassadorId;

    private String name;
    private String email;
    private String level;

    private int score;
    private int rank;

    private Instant generatedAt;

    @Indexed(name = "expireAtIndex", expireAfter = "0s")
    private Instant expireAt;
}
