package com.talentboozt.s_backend.Service.PLAT_COURSES;

import com.talentboozt.s_backend.Model.AMBASSADOR.AmbassadorProfileModel;
import com.talentboozt.s_backend.Model.PLAT_COURSES.AmbassadorTaskProgressModel;
import com.talentboozt.s_backend.Model.PLAT_COURSES.CourseCouponsModel;
import com.talentboozt.s_backend.Model.PLAT_COURSES.GamificationTaskModel;
import com.talentboozt.s_backend.Repository.AMBASSADOR.AmbassadorSessionRepository;
import com.talentboozt.s_backend.Repository.AMBASSADOR.ReferralRepository;
import com.talentboozt.s_backend.Repository.PLAT_COURSES.CourseCouponsRepository;
import com.talentboozt.s_backend.Repository.PLAT_COURSES.TaskProgressRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ProgressUpdater {

    @Autowired
    private ReferralRepository referralRepo;

    @Autowired
    private AmbassadorSessionRepository sessionRepo;

    @Autowired
    private TaskProgressRepository progressRepo;

    @Autowired
    private CourseCouponsRepository courseCouponsRepo;

    @Autowired
    private RewardService rewardService;

    @Transactional
    public void updateProgressForTask(AmbassadorProfileModel ambassador, GamificationTaskModel task) {
        String ambassadorId = ambassador.getId();
        String taskId = task.getId();

        // Calculate progress based on task type
        int currentProgress = switch (task.getType()) {
            case "REFERRAL" -> referralRepo.countByAmbassadorId(ambassadorId);
            case "SESSION_HOSTING" -> sessionRepo.countByAmbassadorIdAndType(ambassadorId, "HOSTED");
            case "TRAINING_ATTENDANCE" -> sessionRepo.countByAmbassadorIdAndType(ambassadorId, "TRAINING");
            default -> 0;
        };

        // Get or create progress
        AmbassadorTaskProgressModel progress = progressRepo
                .findByAmbassadorIdAndTaskId(ambassadorId, taskId)
                .orElseGet(() -> {
                    AmbassadorTaskProgressModel p = new AmbassadorTaskProgressModel();
                    p.setAmbassadorId(ambassadorId);
                    p.setTaskId(taskId);
                    p.setTaskType(task.getType());
                    p.setStartedAt(Instant.now());
                    return p;
                });

        progress.setProgressValue(currentProgress);

        // If newly completed
        if (!progress.isCompleted() && currentProgress >= task.getTargetValue()) {
            progress.setCompleted(true);
            progress.setCompletedAt(Instant.now());
            progress.setRewardStatus("NOT_ISSUED");
        }

        // If eligible for reward
        if (progress.isCompleted() && !progress.isRewarded()) {
            synchronized ((ambassadorId + taskId).intern()) {
                // Ensure reward not already issued
                if (!progressRepo.findByAmbassadorIdAndTaskId(ambassadorId, taskId)
                        .map(AmbassadorTaskProgressModel::isRewarded)
                        .orElse(false)) {
                    rewardService.issueRewardForTask(ambassador, task, progress);
                    unlockCouponsForTask(task, ambassador);

                    progress.setRewarded(true);
                    progress.setRewardedAt(Instant.now());
                    progress.setRewardStatus("ISSUED");
                }
            }
        }

        // Recurring Task Reset (after reward)
        if (task.isRecurring() && task.getFrequencyInDays() > 0) {
            Instant now = Instant.now();
            Instant lastReset = progress.getLastResetAt();
            long frequencyMillis = task.getFrequencyInDays() * 24L * 60 * 60 * 1000;

            boolean shouldReset = lastReset == null ||
                    now.toEpochMilli() - lastReset.toEpochMilli() >= frequencyMillis;

            if (shouldReset) {
                progress.setProgressValue(0);
                progress.setCompleted(false);
                progress.setRewarded(false);
                progress.setStartedAt(now);
                progress.setCompletedAt(null);
                progress.setRewardedAt(null);
                progress.setRewardStatus("NOT_ISSUED");
                progress.setLastResetAt(now);
            }
        }

        // Save once at the end
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
