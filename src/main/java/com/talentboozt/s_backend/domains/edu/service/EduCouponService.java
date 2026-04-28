package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.dto.coupon.CouponValidationResult;
import com.talentboozt.s_backend.domains.edu.exception.EduAccessDeniedException;
import com.talentboozt.s_backend.domains.edu.exception.EduBadRequestException;
import com.talentboozt.s_backend.domains.edu.exception.EduLimitExceededException;
import com.talentboozt.s_backend.domains.edu.exception.EduResourceNotFoundException;
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
public class EduCouponService {

    private final ECouponsRepository couponsRepository;
    private final ECouponRedemptionRepository redemptionRepository;
    private final com.talentboozt.s_backend.domains.subscription.service.FeatureFlagService featureFlagService;
    private final EduFraudDetectionService fraudDetectionService;

    public EduCouponService(ECouponsRepository couponsRepository, 
                          ECouponRedemptionRepository redemptionRepository,
                          com.talentboozt.s_backend.domains.subscription.service.FeatureFlagService featureFlagService,
                          EduFraudDetectionService fraudDetectionService) {
        this.couponsRepository = couponsRepository;
        this.redemptionRepository = redemptionRepository;
        this.featureFlagService = featureFlagService;
        this.fraudDetectionService = fraudDetectionService;
    }

    public ECoupons createCoupon(String creatorId, ECoupons request) {
        if (creatorId == null) {
            throw new EduBadRequestException("Creator ID is required.");
        }
        if (!featureFlagService.isFeatureEnabled(creatorId, "COUPONS")) {
            throw new EduAccessDeniedException("Coupon creation is not available for your current plan.");
        }
        
        // Service-level validations
        if (request.getCode() == null || request.getCode().trim().isEmpty()) {
            throw new EduBadRequestException("Coupon code is required.");
        }
        request.setCode(request.getCode().trim().toUpperCase());
        
        if (couponsRepository.findByCode(request.getCode()).isPresent()) {
            throw new EduBadRequestException("Coupon code already exists.");
        }
        
        if (request.getDiscountValue() == null || request.getDiscountValue() <= 0) {
            throw new EduBadRequestException("Discount value must be greater than zero.");
        }
        
        if (request.getDiscountType() == null || 
            (!request.getDiscountType().equalsIgnoreCase("PERCENTAGE") && 
             !request.getDiscountType().equalsIgnoreCase("FLAT"))) {
            throw new EduBadRequestException("Discount type must be PERCENTAGE or FLAT.");
        }

        if ("PERCENTAGE".equalsIgnoreCase(request.getDiscountType()) && request.getDiscountValue() > 100) {
            throw new EduBadRequestException("Percentage discount cannot exceed 100%.");
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
                .orElseThrow(() -> new EduResourceNotFoundException("Coupon not found with id: " + couponId));

        if (!existing.getCreatorId().equals(creatorId)) {
            throw new EduAccessDeniedException("Not authorized to edit this coupon.");
        }

        // Allow updating code, but must be unique if changed.
        if (request.getCode() != null && !request.getCode().equals(existing.getCode())) {
            if (couponsRepository.findByCode(request.getCode()).isPresent()) {
                throw new EduBadRequestException("Coupon code already exists.");
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
                .orElseThrow(() -> new EduResourceNotFoundException("Coupon not found with id: " + couponId));
        if (!existing.getCreatorId().equals(creatorId)) {
            throw new EduAccessDeniedException("Not authorized to delete this coupon.");
        }
        couponsRepository.deleteById(couponId);
    }

    /**
     * Requirement alias for validateAndCalculate.
     */
    public CouponValidationResult applyCoupon(String code, String courseId, String userId, Double currentPrice) {
        return validateAndCalculate(code, courseId, userId, currentPrice);
    }

    /**
     * Requirement alias for validateAndCalculate.
     */
    public CouponValidationResult validateCoupon(String code, String courseId) {
        // Fallback for UI-only validation where price/user might not be known yet
        return validateAndCalculate(code, courseId, null, 0.0);
    }

    /**
     * Validates a coupon and returns a structured result with all calculation details.
     * Used by EduCoursePurchaseService to apply discounts during checkout.
     *
     * @return CouponValidationResult with originalPrice, discountAmount, finalPrice
     * @throws EduBadRequestException if coupon is invalid, expired, or not applicable
     */
    public CouponValidationResult validateAndCalculate(String code, String courseId, String userId, Double currentPrice) {
        if (userId != null) {
            fraudDetectionService.detectCouponAbuse(userId);
        }
        Optional<ECoupons> opt = couponsRepository.findByCode(code);
        if (opt.isEmpty()) {
            throw new EduResourceNotFoundException("Coupon not found with code: " + code);
        }
        ECoupons coupon = opt.get();

        if (userId != null && userId.equals(coupon.getCreatorId())) {
            throw new EduBadRequestException("Creators cannot use their own coupons.");
        }

        if (userId != null) {
            long usageCount = redemptionRepository.countByUserIdAndCouponId(userId, coupon.getId());
            if (usageCount > 0) {
                throw new EduBadRequestException("You have already redeemed this coupon. Limits apply.");
            }
        }

        if (!Boolean.TRUE.equals(coupon.getIsActive())) {
            throw new EduBadRequestException("Coupon is not active.");
        }

        if (coupon.getExpiresAt() != null && coupon.getExpiresAt().isBefore(Instant.now())) {
            throw new EduBadRequestException("Coupon has expired.");
        }

        if (coupon.getMaxRedemptions() != null && coupon.getCurrentRedemptions() >= coupon.getMaxRedemptions()) {
            throw new EduLimitExceededException("Coupon limit reached.");
        }

        if (coupon.getApplicableCourseIds() != null && coupon.getApplicableCourseIds().length > 0) {
            boolean validForCourse = Arrays.asList(coupon.getApplicableCourseIds()).contains(courseId);
            if (!validForCourse) {
                throw new EduBadRequestException("Coupon is not applicable for this course.");
            }
        }

        double discountAmount;
        if ("PERCENTAGE".equalsIgnoreCase(coupon.getDiscountType())) {
            discountAmount = currentPrice * (coupon.getDiscountValue() / 100.0);
        } else if ("FLAT".equalsIgnoreCase(coupon.getDiscountType())) {
            discountAmount = Math.min(coupon.getDiscountValue(), currentPrice);
        } else {
            discountAmount = 0.0;
        }

        double finalPrice = Math.max(0.0, currentPrice - discountAmount);

        return CouponValidationResult.builder()
                .couponId(coupon.getId())
                .code(coupon.getCode())
                .originalPrice(currentPrice)
                .discountAmount(Math.round(discountAmount * 100.0) / 100.0)
                .finalPrice(Math.round(finalPrice * 100.0) / 100.0)
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .build();
    }

    public void redeemCoupon(String code, String userId, String transactionId) {
        incrementUsage(code, userId, transactionId);
    }

    /**
     * Requirement alias for redeemCoupon.
     */
    public void incrementUsage(String code, String userId, String transactionId) {
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
