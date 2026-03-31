package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.model.ECouponRedemption;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECouponRedemptionRepository;
import com.talentboozt.s_backend.domains.edu.model.ECoupons;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECouponsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EduCouponService {

    private final ECouponsRepository couponsRepository;
    private final ECouponRedemptionRepository redemptionRepository;

    public ECoupons createCoupon(String creatorId, ECoupons request) {
        if (couponsRepository.findByCode(request.getCode()).isPresent()) {
            throw new IllegalArgumentException("Coupon code already exists.");
        }
        request.setCreatorId(creatorId);
        request.setCreatedAt(Instant.now());
        if (request.getIsActive() == null) {
            request.setIsActive(true);
        }
        if (request.getCurrentRedemptions() == null) {
            request.setCurrentRedemptions(0);
        }
        return couponsRepository.save(request);
    }

    public List<ECoupons> getCouponsByCreator(String creatorId) {
        return couponsRepository.findByCreatorId(creatorId);
    }

    public ECoupons updateCoupon(String couponId, String creatorId, ECoupons request) {
        ECoupons existing = couponsRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found."));

        if (!existing.getCreatorId().equals(creatorId)) {
            throw new RuntimeException("Not authorized to edit this coupon.");
        }

        // Allow updating code, but must be unique if changed.
        if (request.getCode() != null && !request.getCode().equals(existing.getCode())) {
            if (couponsRepository.findByCode(request.getCode()).isPresent()) {
                throw new IllegalArgumentException("Coupon code already exists.");
            }
            existing.setCode(request.getCode());
        }

        if (request.getDiscountType() != null) existing.setDiscountType(request.getDiscountType());
        if (request.getDiscountValue() != null) existing.setDiscountValue(request.getDiscountValue());
        if (request.getMaxRedemptions() != null) existing.setMaxRedemptions(request.getMaxRedemptions());
        if (request.getApplicableCourseIds() != null) existing.setApplicableCourseIds(request.getApplicableCourseIds());
        if (request.getIsActive() != null) existing.setIsActive(request.getIsActive());
        if (request.getExpiresAt() != null) existing.setExpiresAt(request.getExpiresAt());

        return couponsRepository.save(existing);
    }

    public void deleteCoupon(String couponId, String creatorId) {
        ECoupons existing = couponsRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found."));
        if (!existing.getCreatorId().equals(creatorId)) {
            throw new RuntimeException("Not authorized to delete this coupon.");
        }
        couponsRepository.deleteById(couponId);
    }

    public Double validateCoupon(String code, String courseId, String userId, Double currentPrice) {
        Optional<ECoupons> opt = couponsRepository.findByCode(code);
        if (opt.isEmpty()) {
            throw new RuntimeException("Coupon not found.");
        }
        ECoupons coupon = opt.get();

        if (userId != null && userId.equals(coupon.getCreatorId())) {
            throw new RuntimeException("Creators cannot use their own coupons.");
        }

        if (userId != null) {
            long usageCount = redemptionRepository.countByUserIdAndCouponId(userId, coupon.getId());
            if (usageCount > 0) {
                throw new RuntimeException("You have already redeemed this coupon. Limits apply.");
            }
        }

        if (!Boolean.TRUE.equals(coupon.getIsActive())) {
            throw new RuntimeException("Coupon is not active.");
        }

        if (coupon.getExpiresAt() != null && coupon.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Coupon has expired.");
        }

        if (coupon.getMaxRedemptions() != null && coupon.getCurrentRedemptions() >= coupon.getMaxRedemptions()) {
            throw new RuntimeException("Coupon limit reached.");
        }

        if (coupon.getApplicableCourseIds() != null && coupon.getApplicableCourseIds().length > 0) {
            boolean validForCourse = Arrays.asList(coupon.getApplicableCourseIds()).contains(courseId);
            if (!validForCourse) {
                throw new RuntimeException("Coupon is not applicable for this course.");
            }
        }

        if ("PERCENTAGE".equalsIgnoreCase(coupon.getDiscountType())) {
            return currentPrice * (coupon.getDiscountValue() / 100.0);
        } else if ("FLAT".equalsIgnoreCase(coupon.getDiscountType())) {
            return Math.min(coupon.getDiscountValue(), currentPrice);
        }

        return 0.0;
    }

    public void redeemCoupon(String code, String userId, String transactionId) {
        couponsRepository.findByCode(code).ifPresent(coupon -> {
            coupon.setCurrentRedemptions((coupon.getCurrentRedemptions() == null ? 0 : coupon.getCurrentRedemptions()) + 1);
            couponsRepository.save(coupon);
            
            // Record usage exactly to stop multi-redemption bypass
            if (userId != null) {
                ECouponRedemption redemption = ECouponRedemption.builder()
                        .userId(userId)
                        .couponId(coupon.getId())
                        .transactionId(transactionId)
                        .redeemedAt(Instant.now())
                        .build();
                redemptionRepository.save(redemption);
            }
        });
    }
}
