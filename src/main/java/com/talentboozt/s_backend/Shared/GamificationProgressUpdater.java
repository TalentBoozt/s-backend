package com.talentboozt.s_backend.Shared;

import com.talentboozt.s_backend.Model.AMBASSADOR.AmbassadorProfileModel;
import com.talentboozt.s_backend.Model.PLAT_COURSES.AmbassadorTaskProgressModel;
import com.talentboozt.s_backend.Model.PLAT_COURSES.GamificationTaskModel;
import com.talentboozt.s_backend.Repository.AMBASSADOR.AmbassadorProfileRepository;
import com.talentboozt.s_backend.Repository.AMBASSADOR.AmbassadorSessionRepository;
import com.talentboozt.s_backend.Repository.AMBASSADOR.ReferralRepository;
import com.talentboozt.s_backend.Repository.PLAT_COURSES.GamificationTaskRepository;
import com.talentboozt.s_backend.Repository.PLAT_COURSES.TaskProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class GamificationProgressUpdater {

    @Autowired
    private AmbassadorProfileRepository ambassadorRepo;

    @Autowired
    private ReferralRepository referralRepo;

    @Autowired
    private AmbassadorSessionRepository sessionRepo;

    @Autowired
    private GamificationTaskRepository taskRepo;

    @Autowired
    private TaskProgressRepository progressRepo;

    @Scheduled(fixedRate = 3600000) // hourly
    public void updateTaskProgressForAllAmbassadors() {
        List<AmbassadorProfileModel> ambassadors = ambassadorRepo.findAll();
        List<GamificationTaskModel> tasks = taskRepo.findAll();

        for (AmbassadorProfileModel ambassador : ambassadors) {
            for (GamificationTaskModel task : tasks) {
                if (task.getLevel() == null || task.getLevel().equals(ambassador.getLevel())) {
                    updateProgressForTask(ambassador, task);
                }
            }
        }
    }

    private void updateProgressForTask(AmbassadorProfileModel ambassador, GamificationTaskModel task) {
        int currentProgress = 0;

        switch (task.getType()) {
            case "REFERRAL":
                currentProgress = referralRepo.countByAmbassadorId(ambassador.getId());
                break;
            case "SESSION_HOSTING":
                currentProgress = sessionRepo.countByAmbassadorIdAndType(ambassador.getId(), "HOSTED");
                break;
            case "TRAINING_ATTENDANCE":
                currentProgress = sessionRepo.countByAmbassadorIdAndType(ambassador.getId(), "TRAINING");
                break;
        }

        AmbassadorTaskProgressModel progress = progressRepo.findByAmbassadorIdAndTaskId(
                ambassador.getId(), task.getId()).orElseGet(() -> {
            AmbassadorTaskProgressModel newProgress = new AmbassadorTaskProgressModel();
            newProgress.setAmbassadorId(ambassador.getId());
            newProgress.setTaskId(task.getId());
            newProgress.setStartedAt(Instant.now());
            return newProgress;
        });

        progress.setProgressValue(currentProgress);

        if (!progress.isCompleted() && currentProgress >= task.getTargetValue()) {
            progress.setCompleted(true);
            progress.setCompletedAt(Instant.now());
            progress.setRewardStatus("NOT_ISSUED");
        }

        progressRepo.save(progress);
    }
}
