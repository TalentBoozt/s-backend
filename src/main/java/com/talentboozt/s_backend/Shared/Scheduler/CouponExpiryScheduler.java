package com.talentboozt.s_backend.Shared.Scheduler;

import com.talentboozt.s_backend.Model.PLAT_COURSES.CourseCouponsModel;
import com.talentboozt.s_backend.Repository.PLAT_COURSES.CourseCouponsRepository;
import com.talentboozt.s_backend.Service.AUDIT_LOGS.SchedulerLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class CouponExpiryScheduler {

    @Autowired
    private CourseCouponsRepository couponRepo;

    @Autowired
    private SchedulerLoggerService logger;

    @Scheduled(fixedRate = 3600000) // every hour
    public void expireExpiredCoupons() {
        Instant now = Instant.now();
        try {
            List<CourseCouponsModel> activeCoupons = couponRepo.findByStatus(CourseCouponsModel.Status.ACTIVE);

            for (CourseCouponsModel coupon : activeCoupons) {
                if (coupon.getExpiresAt() != null && coupon.getExpiresAt().isBefore(now)) {
                    coupon.setStatus(CourseCouponsModel.Status.EXPIRED);
                    coupon.setExpiredAt(now);
                    couponRepo.save(coupon);
                }
            }

            logger.log("couponExpiry", "SUCCESS",
                    "Successfully expired " + activeCoupons.size() + " active coupons.");
        } catch (Exception ex) {
            logger.log("couponExpiry", "ERROR", ex.getMessage());
            throw ex;
        }
    }
}
