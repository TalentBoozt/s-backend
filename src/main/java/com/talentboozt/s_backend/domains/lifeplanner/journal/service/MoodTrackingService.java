package com.talentboozt.s_backend.domains.lifeplanner.journal.service;

import org.springframework.stereotype.Service;
import com.talentboozt.s_backend.domains.lifeplanner.journal.model.MoodEntry;
import com.talentboozt.s_backend.domains.lifeplanner.journal.model.WeeklyMoodSummary;
import com.talentboozt.s_backend.domains.lifeplanner.journal.repository.mongodb.MoodEntryRepository;
import com.talentboozt.s_backend.domains.lifeplanner.journal.repository.mongodb.WeeklyMoodSummaryRepository;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MoodTrackingService {

    private final MoodEntryRepository moodEntryRepository;
    private final WeeklyMoodSummaryRepository weeklyMoodSummaryRepository;

    public MoodEntry logMood(String userId, int score, String label) {
        LocalDate today = LocalDate.now();
        MoodEntry entry = moodEntryRepository.findByUserIdAndDate(userId, today)
                .orElse(new MoodEntry());

        entry.setUserId(userId);
        entry.setDate(today);
        entry.setScore(score);
        entry.setLabel(label);
        if (entry.getId() == null) {
            entry.setCreatedAt(Instant.now());
        }

        MoodEntry saved = moodEntryRepository.save(entry);
        try {
            updateWeeklySummary(userId, today);
        } catch (Exception e) {
            // Log error but don't fail the request
        }
        return saved;
    }

    private void updateWeeklySummary(String userId, LocalDate date) {
        // Find start of the week (assuming Monday)
        LocalDate weekStart = date.with(java.time.DayOfWeek.MONDAY);
        LocalDate weekEnd = weekStart.plusDays(6);

        List<MoodEntry> weekEntries = moodEntryRepository.findByUserIdAndDateBetweenOrderByDateAsc(userId, weekStart, weekEnd);
        if (weekEntries.isEmpty()) return;

        double avg = weekEntries.stream().mapToInt(MoodEntry::getScore).average().orElse(0.0);
        
        // Find most frequent label
        String dominant = weekEntries.stream()
            .collect(java.util.stream.Collectors.groupingBy(MoodEntry::getLabel, java.util.stream.Collectors.counting()))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("Neutral");

        WeeklyMoodSummary summary = weeklyMoodSummaryRepository.findByUserIdAndWeekStartDate(userId, weekStart)
            .orElse(new WeeklyMoodSummary());
        
        summary.setUserId(userId);
        summary.setWeekStartDate(weekStart);
        summary.setWeekEndDate(weekEnd);
        summary.setAverageScore(avg);
        summary.setEntryCount(weekEntries.size());
        summary.setDominantMood(dominant);
        summary.setComputedAt(Instant.now());

        // Trend calculation
        Optional<WeeklyMoodSummary> prevWeek = weeklyMoodSummaryRepository.findByUserIdAndWeekStartDate(userId, weekStart.minusWeeks(1));
        if (prevWeek.isPresent()) {
            double prevAvg = prevWeek.get().getAverageScore();
            if (avg > prevAvg + 0.2) summary.setTrendDirection("UP");
            else if (avg < prevAvg - 0.2) summary.setTrendDirection("DOWN");
            else summary.setTrendDirection("STABLE");
        } else {
            summary.setTrendDirection("STABLE");
        }

        weeklyMoodSummaryRepository.save(summary);
    }

    public Optional<MoodEntry> getTodayMood(String userId) {
        return moodEntryRepository.findByUserIdAndDate(userId, LocalDate.now());
    }

    public List<WeeklyMoodSummary> getWeeklySummaries(String userId) {
        return weeklyMoodSummaryRepository.findByUserIdOrderByWeekStartDateDesc(userId);
    }

    public List<MoodEntry> getMoodTrends(String userId) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(14); // Last 2 weeks
        return moodEntryRepository.findByUserIdAndDateBetweenOrderByDateAsc(userId, start, end);
    }
}
