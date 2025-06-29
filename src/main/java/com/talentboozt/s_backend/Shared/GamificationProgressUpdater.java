package com.talentboozt.s_backend.Shared;

import com.talentboozt.s_backend.Model.AMBASSADOR.AmbassadorProfileModel;
import com.talentboozt.s_backend.Model.PLAT_COURSES.AmbassadorTaskProgressModel;
import com.talentboozt.s_backend.Model.PLAT_COURSES.CourseCouponsModel;
import com.talentboozt.s_backend.Model.PLAT_COURSES.GamificationTaskModel;
import com.talentboozt.s_backend.Repository.AMBASSADOR.AmbassadorProfileRepository;
import com.talentboozt.s_backend.Repository.AMBASSADOR.AmbassadorSessionRepository;
import com.talentboozt.s_backend.Repository.AMBASSADOR.ReferralRepository;
import com.talentboozt.s_backend.Repository.PLAT_COURSES.CourseCouponsRepository;
import com.talentboozt.s_backend.Repository.PLAT_COURSES.GamificationTaskRepository;
import com.talentboozt.s_backend.Repository.PLAT_COURSES.TaskProgressRepository;
import com.talentboozt.s_backend.Service.PLAT_COURSES.RewardService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

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

    @Autowired
    private CourseCouponsRepository courseCouponsRepo;

    @Autowired
    private RewardService rewardService;

    @Scheduled(fixedRate = 3600000) // every hour
    public void updateTaskProgressForAllAmbassadors() {
        List<AmbassadorProfileModel> ambassadors = ambassadorRepo.findAll();
        List<GamificationTaskModel> tasks = taskRepo.findAll();

        for (AmbassadorProfileModel ambassador : ambassadors) {
            for (GamificationTaskModel task : tasks) {
                if (task.getLevel() == null || task.getLevel().isEmpty() || task.getLevel().equalsIgnoreCase(ambassador.getLevel())) {
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

        if (!progress.isRewarded() && progress.isCompleted()) {
            rewardService.issueRewardForTask(ambassador, task, progress);
            unlockCouponsForTask(task, ambassador);
            progress.setRewarded(true);
        }

        progressRepo.save(progress);
    }

    private void unlockCouponsForTask(GamificationTaskModel task, AmbassadorProfileModel ambassador) {
        List<CourseCouponsModel> matchingCoupons = courseCouponsRepo.findByTaskIdAndStatus(task.getId(), CourseCouponsModel.Status.LOCKED);

        for (CourseCouponsModel original : matchingCoupons) {
            CourseCouponsModel unlocked = new CourseCouponsModel();
            BeanUtils.copyProperties(original, unlocked);
            unlocked.setId(null); // Let Mongo generate new ID
            unlocked.setUserId(ambassador.getId());
            unlocked.setUnlockedBy(ambassador.getId());
            unlocked.setUnlockedAt(Instant.now());
            unlocked.setCreatedAt(Instant.now());
            unlocked.setToken(UUID.randomUUID().toString());
            unlocked.setStatus(CourseCouponsModel.Status.UNLOCKED);
            unlocked.setCurrentRedemptions(0);
            unlocked.setEarnedAt(Instant.now());

            courseCouponsRepo.save(unlocked);
        }
    }
}

