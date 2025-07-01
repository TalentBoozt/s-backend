package com.talentboozt.s_backend.Shared.Scheduler;

import com.talentboozt.s_backend.Model.AMBASSADOR.AmbassadorProfileModel;
import com.talentboozt.s_backend.Model.PLAT_COURSES.GamificationTaskModel;
import com.talentboozt.s_backend.Repository.AMBASSADOR.AmbassadorProfileRepository;
import com.talentboozt.s_backend.Repository.PLAT_COURSES.GamificationTaskRepository;
import com.talentboozt.s_backend.Service.PLAT_COURSES.ProgressUpdater;
import com.talentboozt.s_backend.Service.SYS_TRACKING.SchedulerLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GamificationProgressUpdater {

    @Autowired
    private AmbassadorProfileRepository ambassadorRepo;

    @Autowired
    private GamificationTaskRepository taskRepo;

    @Autowired
    private ProgressUpdater progressUpdater;

    @Autowired
    private SchedulerLoggerService logger;

    @Scheduled(fixedRate = 3600000) // every hour
    public void updateTaskProgressForAllAmbassadors() {
        try{
            List<AmbassadorProfileModel> ambassadors = ambassadorRepo.findAll();
            List<GamificationTaskModel> tasks = taskRepo.findAll();

            for (AmbassadorProfileModel ambassador : ambassadors) {
                for (GamificationTaskModel task : tasks) {
                    if (task.getLevel() == null || task.getLevel().isEmpty() || task.getLevel().equalsIgnoreCase(ambassador.getLevel())) {
                        progressUpdater.updateProgressForTask(ambassador, task);
                        logger.log("updateTaskProgress", "SUCCESS", "Task progress" + task.getId() + " updated for ambassador " + ambassador.getId());
                    }
                }
            }

            logger.log("updateTaskProgress", "INFO", "Task progress updated for all ambassadors.");
        } catch (Exception ex) {
            logger.log("updateTaskProgress", "ERROR", ex.getMessage());
        }
    }
}

