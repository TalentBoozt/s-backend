package com.talentboozt.s_backend.Service.PLAT_COURSES;

import com.talentboozt.s_backend.Model.AMBASSADOR.AmbassadorProfileModel;
import com.talentboozt.s_backend.Model.AMBASSADOR.AmbassadorRewardModel;
import com.talentboozt.s_backend.Model.AMBASSADOR.BadgeModel;
import com.talentboozt.s_backend.Model.AMBASSADOR.SwagModel;
import com.talentboozt.s_backend.Model.PLAT_COURSES.AmbassadorTaskProgressModel;
import com.talentboozt.s_backend.Model.PLAT_COURSES.CourseCouponsModel;
import com.talentboozt.s_backend.Model.PLAT_COURSES.GamificationTaskModel;
import com.talentboozt.s_backend.Repository.AMBASSADOR.AmbassadorProfileRepository;
import com.talentboozt.s_backend.Repository.AMBASSADOR.AmbassadorRewardRepository;
import com.talentboozt.s_backend.Repository.AMBASSADOR.BadgeRepository;
import com.talentboozt.s_backend.Repository.AMBASSADOR.SwagRepository;
import com.talentboozt.s_backend.Repository.PLAT_COURSES.CourseCouponsRepository;
import com.talentboozt.s_backend.Service.AUDIT_LOGS.RewardAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class RewardService {

    @Autowired
    private CourseCouponsRepository couponRepo;

    @Autowired
    private AmbassadorRewardRepository rewardRepo;

    @Autowired
    private AmbassadorProfileRepository ambassadorRepo;

    @Autowired
    private BadgeRepository badgeRepo;

    @Autowired
    private SwagRepository swagRepo;

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
            case "BADGE" -> issueBadgeReward(ambassador, task, progress);
            case "SWAG" -> issueSwagReward(ambassador, task, progress);
            case "POINTS" -> addPointsReward(ambassador, task, progress);
            case "DISCOUNT" -> issueGenericReward(ambassador, task.getRewardType(), task.getId());
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

    private void issueBadgeReward(AmbassadorProfileModel ambassador, GamificationTaskModel task, AmbassadorTaskProgressModel progress) {
        String badgeId = Optional.ofNullable(task.getRewardMetadata())
                .map(m -> m.get("badgeId"))
                .map(Object::toString)
                .orElse(task.getId());

        if (badgeRepo.existsByAmbassadorIdAndBadgeId(ambassador.getId(), badgeId)) return;

        BadgeModel badge = new BadgeModel();
        badge.setAmbassadorId(ambassador.getId());
        badge.setTaskId(task.getId());
        badge.setBadgeId(badgeId);
        badge.setTitle(task.getTitle());
        badge.setDescription(task.getDescription());
        badge.setEarnedAt(Instant.now());

        badgeRepo.save(badge);

        auditService.record(
                ambassador,
                task,
                "BADGE",
                badge.getBadgeId(),
                badge.getTitle(),
                "ISSUED",
                "Badge reward issued for task"
        );
    }

    private void issueSwagReward(AmbassadorProfileModel ambassador, GamificationTaskModel task, AmbassadorTaskProgressModel progress) {
        String swagType = Optional.ofNullable(task.getRewardMetadata())
                .map(m -> m.get("swagType"))
                .map(Object::toString)
                .orElse("unknown");

        if (swagRepo.existsByAmbassadorIdAndTaskId(ambassador.getId(), task.getId())) return;

        SwagModel swag = new SwagModel();
        swag.setAmbassadorId(ambassador.getId());
        swag.setTaskId(task.getId());
        swag.setSwagType(swagType);
        swag.setStatus("PENDING");
        swag.setRequestedAt(Instant.now());

        swagRepo.save(swag);

        auditService.record(
                ambassador,
                task,
                "SWAG",
                swag.getId(),
                swag.getSwagType(),
                "ISSUED",
                "Swag reward issued for task"
        );
    }

    private void addPointsReward(AmbassadorProfileModel ambassador, GamificationTaskModel task, AmbassadorTaskProgressModel progress) {
        int points = Optional.ofNullable(task.getRewardMetadata())
                .map(m -> m.get("points"))
                .map(Object::toString)
                .map(Integer::parseInt)
                .orElse(0);

        ambassador.setPoints(ambassador.getPoints() + points);
        ambassador.setLastPointEarnedAt(Instant.now());
        ambassadorRepo.save(ambassador);

        auditService.record(
                ambassador,
                task,
                "POINTS",
                String.valueOf(points),
                "Points",
                "ISSUED",
                "Points added for completed task"
        );
    }
}
