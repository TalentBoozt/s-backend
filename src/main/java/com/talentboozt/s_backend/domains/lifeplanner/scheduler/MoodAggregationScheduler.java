package com.talentboozt.s_backend.domains.lifeplanner.scheduler;

import com.talentboozt.s_backend.domains.lifeplanner.journal.model.MoodEntry;
import com.talentboozt.s_backend.domains.lifeplanner.journal.model.WeeklyMoodSummary;
import com.talentboozt.s_backend.domains.lifeplanner.journal.repository.mongodb.WeeklyMoodSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Runs nightly at 1 AM. Aggregates mood entries from the past week
 * into a WeeklyMoodSummary per user.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MoodAggregationScheduler {

    private final MongoTemplate mongoTemplate;
    private final WeeklyMoodSummaryRepository weeklyMoodSummaryRepository;

    @Scheduled(cron = "${lifeplanner.scheduler.mood-aggregation-cron:0 0 1 * * ?}")
    public void aggregateWeeklyMoods() {
        log.info("[LifePlanner CRON] Starting mood aggregation...");

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);

        // Query all mood entries from the past week
        Query query = new Query(Criteria.where("date").gte(weekStart).lte(weekEnd));
        List<MoodEntry> entries = mongoTemplate.find(query, MoodEntry.class);

        // Group by userId
        Map<String, List<MoodEntry>> byUser = entries.stream()
                .collect(Collectors.groupingBy(MoodEntry::getUserId));

        int summaryCount = 0;
        for (Map.Entry<String, List<MoodEntry>> entry : byUser.entrySet()) {
            String userId = entry.getKey();
            List<MoodEntry> userEntries = entry.getValue();

            double avgScore = userEntries.stream()
                    .mapToInt(MoodEntry::getScore)
                    .average()
                    .orElse(3.0);

            String dominantMood = userEntries.stream()
                    .collect(Collectors.groupingBy(MoodEntry::getLabel, Collectors.counting()))
                    .entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("Okay");

            // Determine trend by comparing with previous week
            String trend = determineTrend(userId, weekStart.minusDays(7), avgScore);

            WeeklyMoodSummary summary = weeklyMoodSummaryRepository
                    .findByUserIdAndWeekStartDate(userId, weekStart)
                    .orElse(new WeeklyMoodSummary());

            summary.setUserId(userId);
            summary.setWeekStartDate(weekStart);
            summary.setWeekEndDate(weekEnd);
            summary.setAverageScore(Math.round(avgScore * 100.0) / 100.0);
            summary.setEntryCount(userEntries.size());
            summary.setDominantMood(dominantMood);
            summary.setTrendDirection(trend);
            summary.setComputedAt(Instant.now());

            weeklyMoodSummaryRepository.save(summary);
            summaryCount++;
        }

        log.info("[LifePlanner CRON] Mood aggregation complete. {} user summaries generated.", summaryCount);
    }

    private String determineTrend(String userId, LocalDate previousWeekStart, double currentAvg) {
        return weeklyMoodSummaryRepository
                .findByUserIdAndWeekStartDate(userId, previousWeekStart)
                .map(prevSummary -> {
                    double diff = currentAvg - prevSummary.getAverageScore();
                    if (diff > 0.3) return "UP";
                    if (diff < -0.3) return "DOWN";
                    return "STABLE";
                })
                .orElse("STABLE");
    }
}
