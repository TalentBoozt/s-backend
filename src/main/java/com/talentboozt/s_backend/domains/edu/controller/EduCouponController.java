package com.talentboozt.s_backend.domains.edu.controller;

import jakarta.validation.Valid;
import com.talentboozt.s_backend.domains.edu.model.ECoupons;
import com.talentboozt.s_backend.domains.edu.service.EduCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/edu/coupons")
@RequiredArgsConstructor
public class EduCouponController {

    private final EduCouponService couponService;
    private final com.talentboozt.s_backend.shared.security.utils.SecurityUtils securityUtils;

    @PostMapping
    @PreAuthorize("hasAuthority('SELLER_FREE') or hasAuthority('ENTERPRISE_INSTRUCTOR')")
    public ResponseEntity<ECoupons> createCoupon(@Valid @RequestBody ECoupons request) {
        String creatorId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(couponService.createCoupon(creatorId, request));
    }

    @GetMapping("/creator")
    @PreAuthorize("hasAuthority('SELLER_FREE') or hasAuthority('ENTERPRISE_INSTRUCTOR')")
    public ResponseEntity<List<ECoupons>> getCouponsByCreator() {
        String creatorId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(couponService.getCouponsByCreator(creatorId));
    }

    @PutMapping("/{couponId}")
    @PreAuthorize("hasAuthority('SELLER_FREE') or hasAuthority('ENTERPRISE_INSTRUCTOR')")
    public ResponseEntity<ECoupons> updateCoupon(
            @PathVariable String couponId,
            @Valid @RequestBody ECoupons request) {
        String creatorId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(couponService.updateCoupon(couponId, creatorId, request));
    }

    @DeleteMapping("/{couponId}")
    @PreAuthorize("hasAuthority('SELLER_FREE') or hasAuthority('ENTERPRISE_INSTRUCTOR')")
    public ResponseEntity<Void> deleteCoupon(@PathVariable String couponId) {
        String creatorId = securityUtils.getCurrentUserId();
        couponService.deleteCoupon(couponId, creatorId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateCoupon(
            @Valid @RequestBody Map<String, Object> request) {
        String code = (String) request.get("code");
        String courseId = (String) request.get("courseId");
        
        Object currentPriceObj = request.get("currentPrice");
        Double currentPrice = currentPriceObj != null ? Double.parseDouble(currentPriceObj.toString()) : 0.0;
        
        String userId = (String) request.get("userId");
        if (userId == null) {
            userId = securityUtils.getCurrentUserId();
        }
        
        com.talentboozt.s_backend.domains.edu.dto.coupon.CouponValidationResult result = couponService.applyCoupon(code, courseId, userId, currentPrice);
        return ResponseEntity.ok(Map.of(
            "valid", true,
            "discountAmount", result.getDiscountAmount(),
            "finalPrice", result.getFinalPrice(),
            "details", result
        ));
    }
}
