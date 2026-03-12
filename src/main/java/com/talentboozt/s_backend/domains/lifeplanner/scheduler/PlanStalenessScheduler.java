package com.talentboozt.s_backend.domains.lifeplanner.scheduler;

import com.talentboozt.s_backend.domains.lifeplanner.planner.model.StudyPlan;
import com.talentboozt.s_backend.domains.lifeplanner.planner.repository.mongodb.StudyPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Runs every Monday at 9 AM. Checks for ACTIVE plans with no progress
 * updates in the last 7 days and marks them as STALE.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PlanStalenessScheduler {

    private final StudyPlanRepository studyPlanRepository;

    @Scheduled(cron = "${lifeplanner.scheduler.plan-staleness-cron:0 0 9 ? * MON}")
    public void checkStalePlans() {
        log.info("[LifePlanner CRON] Starting plan staleness check...");

        List<StudyPlan> activePlans = studyPlanRepository.findByStatus("ACTIVE");
        Instant threshold = Instant.now().minus(7, ChronoUnit.DAYS);
        int staleCount = 0;

        for (StudyPlan plan : activePlans) {
            if (plan.getUpdatedAt() != null && plan.getUpdatedAt().isBefore(threshold)) {
                plan.setStatus("STALE");
                plan.setUpdatedAt(Instant.now());
                studyPlanRepository.save(plan);
                staleCount++;
                log.info("Marked plan {} as STALE (last updated: {})", plan.getPlanId(), plan.getUpdatedAt());
            }
        }

        log.info("[LifePlanner CRON] Staleness check complete. {} plans marked stale.", staleCount);
    }
}
