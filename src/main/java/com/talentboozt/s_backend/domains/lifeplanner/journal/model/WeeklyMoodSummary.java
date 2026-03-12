package com.talentboozt.s_backend.domains.lifeplanner.journal.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.time.Instant;

@Data
@Document(collection = "lp_weekly_mood_summaries")
@CompoundIndex(name = "user_week_idx", def = "{'userId': 1, 'weekStartDate': 1}", unique = true)
public class WeeklyMoodSummary {
    @Id
    private String id;
    private String userId;
    private LocalDate weekStartDate;
    private LocalDate weekEndDate;
    private double averageScore;
    private int entryCount;
    private String dominantMood;
    private String trendDirection; // UP, DOWN, STABLE
    private Instant computedAt;
}
