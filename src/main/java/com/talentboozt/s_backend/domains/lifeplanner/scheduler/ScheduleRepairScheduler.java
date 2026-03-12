package com.talentboozt.s_backend.domains.lifeplanner.scheduler;


import com.talentboozt.s_backend.domains.lifeplanner.planner.model.StudyPlan;
import com.talentboozt.s_backend.domains.lifeplanner.planner.repository.mongodb.StudyPlanRepository;
import com.talentboozt.s_backend.domains.lifeplanner.planner.service.ScheduleRepairService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Runs daily at 6 AM. Scans all ACTIVE study plans and triggers
 * schedule repair for any plan with uncompleted tasks from past days.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleRepairScheduler {

    private final StudyPlanRepository studyPlanRepository;
    private final ScheduleRepairService scheduleRepairService;

    @Scheduled(cron = "${lifeplanner.scheduler.schedule-repair-cron:0 0 6 * * ?}")
    public void runDailyScheduleRepair() {
        log.info("[LifePlanner CRON] Starting daily schedule repair scan...");

        List<StudyPlan> activePlans = studyPlanRepository.findByStatus("ACTIVE");

        int repairedCount = 0;
        for (StudyPlan plan : activePlans) {
            try {
                var result = scheduleRepairService.repairSchedule(plan.getPlanId(), null);
                if (!result.getRescheduledTasks().isEmpty()) {
                    repairedCount++;
                    log.info("Repaired schedule for plan: {} ({} tasks rescheduled)",
                            plan.getPlanId(), result.getRescheduledTasks().size());
                }
            } catch (Exception e) {
                log.error("Failed to repair schedule for plan: {}", plan.getPlanId(), e);
            }
        }

        log.info("[LifePlanner CRON] Schedule repair complete. {} plans repaired out of {} active.",
                repairedCount, activePlans.size());
    }
}
