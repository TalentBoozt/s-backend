package com.talentboozt.s_backend.shared.scheduler;

import com.talentboozt.s_backend.domains.ambassador.service.LeaderboardService;
import com.talentboozt.s_backend.domains.audit_logs.service.SchedulerLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LeaderboardScheduler {

    @Autowired
    private LeaderboardService leaderboardService;

    @Autowired
    private SchedulerLoggerService logger;

    @Scheduled(fixedRate = 12 * 60 * 60 * 1000) // every 12 hours
    public void updateLeaderboards() {
        try {
            leaderboardService.generateLeaderboards();
            logger.log("leaderboardUpdate", "SUCCESS", "Leaderboards updated successfully.");
        } catch (Exception ex) {
            logger.log("leaderboardUpdate", "ERROR", ex.getMessage());
        }
    }
}
