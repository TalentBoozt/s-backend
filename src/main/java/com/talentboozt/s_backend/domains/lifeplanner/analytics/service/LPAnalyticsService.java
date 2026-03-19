package com.talentboozt.s_backend.domains.lifeplanner.analytics.service;

import org.springframework.stereotype.Service;
import com.talentboozt.s_backend.domains.lifeplanner.analytics.dto.LPAnalyticsDTO;
import com.talentboozt.s_backend.domains.lifeplanner.planner.model.DailySchedule;
import com.talentboozt.s_backend.domains.lifeplanner.planner.repository.mongodb.DailyScheduleRepository;
import com.talentboozt.s_backend.domains.lifeplanner.planner.service.StreakService;
import com.talentboozt.s_backend.domains.lifeplanner.journal.model.MoodEntry;
import com.talentboozt.s_backend.domains.lifeplanner.journal.repository.mongodb.MoodEntryRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LPAnalyticsService {

    private final DailyScheduleRepository dailyScheduleRepository;
    private final MoodEntryRepository moodEntryRepository;
    private final StreakService streakService;

    public LPAnalyticsDTO getAnalytics(String userId) {
        List<DailySchedule> allSchedules = dailyScheduleRepository.findByUserId(userId);

        // Flatten all tasks
        List<DailySchedule.ScheduleTask> allTasks = allSchedules.stream()
                .flatMap(s -> s.getTasks().stream())
                .collect(Collectors.toList());

        int totalCompleted = (int) allTasks.stream().filter(DailySchedule.ScheduleTask::isCompleted).count();
        int totalPending = allTasks.size() - totalCompleted;
        double completionRate = allTasks.isEmpty() ? 0 : ((double) totalCompleted / allTasks.size()) * 100;

        // Streak
        int currentStreak = streakService.calculateCurrentStreak(userId);

        // Best streak (iterate all completed schedules sorted by date)
        int bestStreak = calculateBestStreak(allSchedules);

        // Category breakdown
        Map<String, Integer> categoryBreakdown = allTasks.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getCategory() != null ? t.getCategory() : "OTHER",
                        Collectors.summingInt(t -> 1)
                ));

        // Priority breakdown
        Map<String, Integer> priorityBreakdown = allTasks.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getPriority() != null ? t.getPriority() : "NONE",
                        Collectors.summingInt(t -> 1)
                ));

        // Last 14 days daily stats
        LocalDate today = LocalDate.now();
        LocalDate twoWeeksAgo = today.minusDays(13);
        List<MoodEntry> moodEntries = moodEntryRepository.findByUserIdAndDateBetweenOrderByDateAsc(userId, twoWeeksAgo, today);
        Map<LocalDate, Integer> moodByDate = moodEntries.stream()
                .collect(Collectors.toMap(MoodEntry::getDate, MoodEntry::getScore, (a, b) -> b));

        List<LPAnalyticsDTO.DailyStats> dailyStats = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            LocalDate date = twoWeeksAgo.plusDays(i);
            DailySchedule schedule = allSchedules.stream()
                    .filter(s -> s.getScheduleDate().equals(date))
                    .findFirst().orElse(null);

            int completed = 0;
            int total = 0;
            if (schedule != null) {
                total = schedule.getTasks().size();
                completed = (int) schedule.getTasks().stream().filter(DailySchedule.ScheduleTask::isCompleted).count();
            }

            dailyStats.add(LPAnalyticsDTO.DailyStats.builder()
                    .date(date.toString())
                    .tasksCompleted(completed)
                    .totalTasks(total)
                    .moodScore(moodByDate.get(date))
                    .build());
        }

        // Average mood
        double avgMood = moodEntries.isEmpty() ? 0 : moodEntries.stream().mapToInt(MoodEntry::getScore).average().orElse(0);

        // Study hours this week (sum estimatedTime mins for completed tasks this week)
        LocalDate weekStart = today.with(java.time.DayOfWeek.MONDAY);
        double studyHoursThisWeek = allSchedules.stream()
                .filter(s -> !s.getScheduleDate().isBefore(weekStart))
                .flatMap(s -> s.getTasks().stream())
                .filter(DailySchedule.ScheduleTask::isCompleted)
                .mapToDouble(t -> {
                    try {
                        String raw = t.getEstimatedTime().replaceAll("[^0-9]", "");
                        return raw.isEmpty() ? 0 : Double.parseDouble(raw) / 60.0;
                    } catch (Exception e) { return 0; }
                })
                .sum();

        return LPAnalyticsDTO.builder()
                .totalTasksCompleted(totalCompleted)
                .totalTasksPending(totalPending)
                .completionRate(Math.round(completionRate * 10.0) / 10.0)
                .currentStreak(currentStreak)
                .bestStreak(bestStreak)
                .averageMood(Math.round(avgMood * 10.0) / 10.0)
                .dailyStats(dailyStats)
                .categoryBreakdown(categoryBreakdown)
                .priorityBreakdown(priorityBreakdown)
                .studyHoursThisWeek(Math.round(studyHoursThisWeek * 10.0) / 10.0)
                .build();
    }

    private int calculateBestStreak(List<DailySchedule> schedules) {
        List<LocalDate> completedDates = schedules.stream()
                .filter(DailySchedule::isCompleted)
                .map(DailySchedule::getScheduleDate)
                .sorted()
                .distinct()
                .collect(Collectors.toList());

        if (completedDates.isEmpty()) return 0;

        int best = 1, current = 1;
        for (int i = 1; i < completedDates.size(); i++) {
            if (completedDates.get(i).equals(completedDates.get(i - 1).plusDays(1))) {
                current++;
                best = Math.max(best, current);
            } else {
                current = 1;
            }
        }
        return best;
    }
}
