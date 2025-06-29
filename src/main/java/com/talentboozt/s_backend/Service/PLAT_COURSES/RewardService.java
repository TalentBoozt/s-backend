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
import java.util.UUID;

@Service
public class RewardService {

    @Autowired
    private CourseCouponsRepository couponRepo;

    @Autowired
    private AmbassadorRewardRepository rewardRepo;

    public void issueRewardForTask(AmbassadorProfileModel ambassador, GamificationTaskModel task, AmbassadorTaskProgressModel progress) {
        String rewardType = task.getRewardType();

        switch (rewardType) {
            case "COUPON":
                issueCouponReward(ambassador, task, progress);
                break;

            case "SWAG":
            case "BADGE":
            case "DISCOUNT":
                issueGenericReward(ambassador, rewardType, task.getId());
                break;
        }

        progress.setRewarded(true);
        progress.setRewardedAt(Instant.now());
    }

    private void issueCouponReward(AmbassadorProfileModel ambassador, GamificationTaskModel task, AmbassadorTaskProgressModel progress) {
        CourseCouponsModel coupon = new CourseCouponsModel();

        coupon.setUserId(ambassador.getEmployeeId());
        coupon.setPublicity(false);
        coupon.setToken(UUID.randomUUID().toString());
        coupon.setCode("TASK-" + task.getTitle().toUpperCase().replace(" ", "-"));
        coupon.setDiscount("10"); // or from task meta
        coupon.setDiscountType("percentage");
        coupon.setCreatedAt(Instant.now());
        coupon.setEarnedAt(Instant.now());
        coupon.setValidityInMillis(30L * 24 * 60 * 60 * 1000); // 30 days
        coupon.setStatus(CourseCouponsModel.Status.CREATED);
        coupon.setTaskId(task.getId());
        coupon.setType("reward");
        coupon.setActivationType("manual");

        couponRepo.save(coupon);
    }

    private void issueGenericReward(AmbassadorProfileModel ambassador, String rewardType, String taskId) {
        AmbassadorRewardModel reward = new AmbassadorRewardModel();
        reward.setAmbassadorId(ambassador.getId());
        reward.setRewardType(rewardType);
        reward.setStatus("PENDING");
        reward.setIssuedAt(Instant.now());

        rewardRepo.save(reward);
    }
}
