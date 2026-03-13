package com.talentboozt.s_backend.domains.lifeplanner.planner.service;

import com.talentboozt.s_backend.domains.lifeplanner.planner.model.DailySchedule;
import com.talentboozt.s_backend.domains.lifeplanner.planner.repository.mongodb.DailyScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StreakService {

    private final DailyScheduleRepository dailyScheduleRepository;

    public int calculateCurrentStreak(String userId) {
        List<DailySchedule> schedules = dailyScheduleRepository.findByUserId(userId)
                .stream()
                .filter(DailySchedule::isCompleted)
                .sorted(Comparator.comparing(DailySchedule::getScheduleDate).reversed())
                .collect(Collectors.toList());

        if (schedules.isEmpty()) return 0;

        int streak = 0;
        LocalDate expectedDate = LocalDate.now();

        // Check if today is completed. If not, start check from yesterday.
        if (!schedules.get(0).getScheduleDate().equals(expectedDate)) {
            expectedDate = expectedDate.minusDays(1);
        }

        for (DailySchedule s : schedules) {
            if (s.getScheduleDate().equals(expectedDate)) {
                streak++;
                expectedDate = expectedDate.minusDays(1);
            } else if (s.getScheduleDate().isBefore(expectedDate)) {
                // Gap found
                break;
            }
        }

        return streak;
    }
}
