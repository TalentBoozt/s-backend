package com.talentboozt.s_backend.domains.plat_courses.service;

import com.talentboozt.s_backend.domains.plat_courses.dto.CouponRedemptionRequest;
import com.talentboozt.s_backend.domains.plat_courses.model.CourseCouponsModel;
import com.talentboozt.s_backend.domains.plat_courses.repository.CourseCouponsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class CourseCouponsService {

    @Autowired
    private CourseCouponsRepository couponRepo;

    public CourseCouponsModel addCourseCoupon(CourseCouponsModel coupon) {
        coupon.setCreatedAt(Instant.now());
        coupon.setStatus(CourseCouponsModel.Status.LOCKED);
        return couponRepo.save(coupon);
    }

    public CourseCouponsModel getCourseCoupon(String id) {
        return couponRepo.findById(id).orElse(null);
    }

    public Iterable<CourseCouponsModel> getAllCourseCoupons() {
        return couponRepo.findAll();
    }

    public List<CourseCouponsModel> getCouponsByUser(String userId) {
        return couponRepo.findByUserId(userId);
    }

    public CourseCouponsModel unlockCoupon(String couponId, String userId) {
        CourseCouponsModel coupon = couponRepo.findById(couponId).orElseThrow();
        if (coupon.getStatus() == CourseCouponsModel.Status.LOCKED) {
            coupon.setStatus(CourseCouponsModel.Status.UNLOCKED);
            coupon.setUnlockedBy(userId);
            coupon.setEarnedAt(Instant.now());
            coupon.setUnlockedAt(Instant.now());
            coupon.setUserId(userId);
            return couponRepo.save(coupon);
        }
        return coupon;
    }

    public CourseCouponsModel activateCoupon(String couponId, String userId) {
        CourseCouponsModel coupon = couponRepo.findById(couponId)
                .orElseThrow(() -> new NoSuchElementException("Coupon not found"));

        if (!userId.equals(coupon.getUserId())) {
            throw new IllegalStateException("Unauthorized: Not the owner of this coupon");
        }

        if (coupon.getStatus() == CourseCouponsModel.Status.ACTIVE) {
            throw new IllegalStateException("Coupon is already active");
        }

        if (coupon.getStatus() != CourseCouponsModel.Status.UNLOCKED) {
            throw new IllegalStateException("Coupon must be in UNLOCKED state to activate");
        }

        Instant now = Instant.now();
        coupon.setStatus(CourseCouponsModel.Status.ACTIVE);
        coupon.setRedeemedAt(now); // Using as activation time
        coupon.setExpiresAt(now.plusMillis(coupon.getValidityInMillis()));
        coupon.setToken(UUID.randomUUID().toString());

        return couponRepo.save(coupon);
    }

    public CourseCouponsModel redeemCoupon(CouponRedemptionRequest request) {
        CourseCouponsModel coupon = couponRepo.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (!coupon.getUserId().equals(request.getUserId())) {
            throw new RuntimeException("Unauthorized user for this coupon.");
        }

        if (coupon.getStatus() != CourseCouponsModel.Status.ACTIVE ||
                coupon.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Coupon expired or not active.");
        }

        coupon.setStatus(CourseCouponsModel.Status.REDEEMED);
        coupon.setRedeemedBy(request.getUserId());
        coupon.setRedeemedForCourseId(request.getCourseId());
        coupon.setRedeemedForInstallmentId(request.getInstallmentId());
        coupon.setExpiredAt(Instant.now());

        return couponRepo.save(coupon);
    }

    public void deleteCourseCoupon(String id) {
        couponRepo.deleteById(id);
    }
}
