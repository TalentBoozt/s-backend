package com.talentboozt.s_backend.shared.scheduler;

import com.talentboozt.s_backend.domains.ambassador.model.AmbassadorProfileModel;
import com.talentboozt.s_backend.domains.ambassador.repository.mongodb.AmbassadorProfileRepository;
import com.talentboozt.s_backend.domains.plat_courses.model.GamificationTaskModel;
import com.talentboozt.s_backend.domains.plat_courses.repository.mongodb.GamificationTaskRepository;
import com.talentboozt.s_backend.domains.plat_courses.service.ProgressUpdater;
import com.talentboozt.s_backend.domains.audit_logs.service.SchedulerLoggerService;
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

