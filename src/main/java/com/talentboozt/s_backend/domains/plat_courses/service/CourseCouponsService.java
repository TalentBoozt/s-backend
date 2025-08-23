package com.talentboozt.s_backend.domains.plat_courses.service;

import com.talentboozt.s_backend.domains.plat_courses.cfg.CouponValidationException;
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
                .orElseThrow(() -> new CouponValidationException("Coupon not found", "COUPON_NOT_FOUND"));

        if (!userId.equals(coupon.getUserId())) {
            throw new CouponValidationException("Unauthorized: Not the owner of this coupon", "UNAUTHORIZED");
        }

        if (coupon.getStatus() == CourseCouponsModel.Status.ACTIVE) {
            throw new CouponValidationException("Coupon already active", "COUPON_ALREADY_ACTIVE");
        }

        if (coupon.getStatus() != CourseCouponsModel.Status.UNLOCKED) {
            throw new CouponValidationException("Coupon must be in UNLOCKED state to activate", "COUPON_NOT_UNLOCKED");
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
                .orElseThrow(() -> new CouponValidationException("Coupon not found", "COUPON_NOT_FOUND"));

        if (coupon.getStatus() != CourseCouponsModel.Status.ACTIVE ||
                coupon.getExpiresAt().isBefore(Instant.now())) {
            throw new CouponValidationException("Coupon expired or not active", "COUPON_EXPIRED");
        }

        if (!coupon.isPublicity()) {
            // Private coupon: must match userId and mark as redeemed
            if (!coupon.getUserId().equals(request.getUserId())) {
                throw new CouponValidationException("Unauthorized: Not the owner of this coupon", "UNAUTHORIZED");
            }
            coupon.setStatus(CourseCouponsModel.Status.REDEEMED);
            coupon.setRedeemedBy(request.getUserId());
            coupon.setRedeemedAt(Instant.now());
        } else {
            // Public coupon: allow multiple redemptions
            int current = coupon.getCurrentRedemptions();
            int max = coupon.getMaxRedemptions();
            if (max > 0 && current >= max) {
                throw new CouponValidationException("Coupon usage limit reached.", "COUPON_USAGE_LIMIT");
            }

            coupon.setCurrentRedemptions(current + 1);
        }

        coupon.setRedeemedForCourseId(request.getCourseId());
        coupon.setRedeemedForInstallmentId(request.getInstallmentId());
        coupon.setExpiredAt(Instant.now());

        return couponRepo.save(coupon);
    }

    public void deleteCourseCoupon(String id) {
        couponRepo.deleteById(id);
    }

    public CourseCouponsModel findValidCouponByCode(String code, String userId, String courseId, String installmentId) {
        CourseCouponsModel coupon = couponRepo.findByCode(code)
                .orElseThrow(() -> new CouponValidationException("Coupon not found", "COUPON_NOT_FOUND"));

        // 1. Check status
        if (coupon.getStatus() != CourseCouponsModel.Status.ACTIVE) {
            throw new CouponValidationException("Coupon is not active.", "COUPON_INACTIVE");
        }

        // 2. Expiration check
        if (coupon.getExpiresAt() != null && coupon.getExpiresAt().isBefore(Instant.now())) {
            throw new CouponValidationException("Coupon expired.", "COUPON_EXPIRED");
        }

        // 3. Check if it's private and belongs to the user
        if (!coupon.isPublicity()) {
            if (!userId.equals(coupon.getUserId())) {
                throw new CouponValidationException("You do not own this coupon.", "COUPON_UNAUTHORIZED");
            }

            if (coupon.getRedeemedAt() != null) {
                throw new CouponValidationException("Coupon already redeemed.", "COUPON_ALREADY_USED");
            }

        } else {
            // 4. For public coupons: enforce redemption limits
            if (coupon.getMaxRedemptions() > 0 && coupon.getCurrentRedemptions() >= coupon.getMaxRedemptions()) {
                throw new CouponValidationException("Coupon usage limit reached.", "COUPON_USAGE_LIMIT");
            }
        }

        // 5. Validate course ID
        List<String> applicableCourses = coupon.getApplicableCourseIds();
        if (applicableCourses != null && !applicableCourses.isEmpty()) {
            if (!applicableCourses.contains(courseId)) {
                throw new CouponValidationException("Coupon not valid for this course.", "COUPON_INVALID_COURSE");
            }
        }

        // 6. Validate installment rules
        if (!coupon.isApplicableForInstallment() && installmentId != null) {
            throw new CouponValidationException("Coupon not allowed for installments.", "COUPON_NOT_FOR_INSTALLMENT");
        }

        return coupon;
    }
}
