package com.talentboozt.s_backend.Service.PLAT_COURSES;

import com.talentboozt.s_backend.Model.AMBASSADOR.AmbassadorProfileModel;
import com.talentboozt.s_backend.Model.AMBASSADOR.AmbassadorRewardModel;
import com.talentboozt.s_backend.Model.PLAT_COURSES.AmbassadorTaskProgressModel;
import com.talentboozt.s_backend.Model.PLAT_COURSES.CourseCouponsModel;
import com.talentboozt.s_backend.Model.PLAT_COURSES.GamificationTaskModel;
import com.talentboozt.s_backend.Repository.AMBASSADOR.AmbassadorRewardRepository;
import com.talentboozt.s_backend.Repository.PLAT_COURSES.CourseCouponsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class RewardService {

    @Autowired
    private CourseCouponsRepository couponRepo;

    @Autowired
    private AmbassadorRewardRepository rewardRepo;

    @Autowired
    private RewardAuditService auditService;

    public void issueRewardForTask(AmbassadorProfileModel ambassador, GamificationTaskModel task, AmbassadorTaskProgressModel progress) {
        if (progress.isRewarded()) return; // Already rewarded

        // Optional: check if reward already exists for safety
        boolean alreadyIssued = switch (task.getRewardType()) {
            case "COUPON" -> couponRepo.existsByUserIdAndTaskId(ambassador.getEmployeeId(), task.getId());
            case "BADGE", "SWAG", "DISCOUNT" -> rewardRepo.existsByAmbassadorIdAndTaskId(ambassador.getId(), task.getId());
            default -> false;
        };

        if (alreadyIssued) return;

        switch (task.getRewardType()) {
            case "COUPON" -> issueCouponReward(ambassador, task, progress);
            case "BADGE", "SWAG", "DISCOUNT" -> issueGenericReward(ambassador, task.getRewardType(), task.getId());
        }

        progress.setRewarded(true);
        progress.setRewardedAt(Instant.now());
        progress.setRewardStatus("ISSUED");
    }

    private void issueCouponReward(AmbassadorProfileModel ambassador, GamificationTaskModel task, AmbassadorTaskProgressModel progress) {
        Map<String, Object> metadata = task.getRewardMetadata() != null ? task.getRewardMetadata() : new HashMap<>();

        // Fallback defaults
        String discount = metadata.getOrDefault("discount", "10").toString(); // Default: 10%
        String discountType = metadata.getOrDefault("discountType", "percentage").toString(); // Default: percentage
        long validityInDays = 30;
        if (metadata.containsKey("validityInDays")) {
            try {
                validityInDays = Long.parseLong(metadata.get("validityInDays").toString());
            } catch (Exception ignored) {}
        }

        CourseCouponsModel coupon = new CourseCouponsModel();
        coupon.setUserId(ambassador.getEmployeeId());
        coupon.setPublicity(false);
        coupon.setToken(UUID.randomUUID().toString());
        coupon.setCode("TASK-" + task.getTitle().toUpperCase().replace(" ", "-"));
        coupon.setDiscount(discount);
        coupon.setDiscountType(discountType);
        coupon.setCreatedAt(Instant.now());
        coupon.setEarnedAt(Instant.now());
        coupon.setValidityInMillis(validityInDays * 24 * 60 * 60 * 1000); // Convert days to millis
        coupon.setStatus(CourseCouponsModel.Status.CREATED);
        coupon.setTaskId(task.getId());
        coupon.setType("reward");
        coupon.setActivationType("manual");

        couponRepo.save(coupon);

        auditService.record(
                ambassador,
                task,
                "COUPON",
                coupon.getId(),
                coupon.getCode(),
                "ISSUED",
                "Coupon reward issued for task"
        );
    }

    private void issueGenericReward(AmbassadorProfileModel ambassador, String rewardType, String taskId) {
        AmbassadorRewardModel reward = new AmbassadorRewardModel();
        reward.setAmbassadorId(ambassador.getId());
        reward.setTaskId(taskId);
        reward.setRewardType(rewardType);
        reward.setStatus("PENDING");
        reward.setIssuedAt(Instant.now());

        rewardRepo.save(reward);
    }
}
