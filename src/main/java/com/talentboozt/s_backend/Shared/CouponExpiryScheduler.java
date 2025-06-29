package com.talentboozt.s_backend.Shared;

import com.talentboozt.s_backend.Model.PLAT_COURSES.CourseCouponsModel;
import com.talentboozt.s_backend.Repository.PLAT_COURSES.CourseCouponsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class CouponExpiryScheduler {

    @Autowired
    private CourseCouponsRepository couponRepo;

    @Scheduled(fixedRate = 3600000) // every hour
    public void expireExpiredCoupons() {
        Instant now = Instant.now();
        List<CourseCouponsModel> activeCoupons = couponRepo.findByStatus(CourseCouponsModel.Status.ACTIVE);

        for (CourseCouponsModel coupon : activeCoupons) {
            if (coupon.getExpiresAt() != null && coupon.getExpiresAt().isBefore(now)) {
                coupon.setStatus(CourseCouponsModel.Status.EXPIRED);
                coupon.setExpiredAt(now);
                couponRepo.save(coupon);
            }
        }
    }
}
