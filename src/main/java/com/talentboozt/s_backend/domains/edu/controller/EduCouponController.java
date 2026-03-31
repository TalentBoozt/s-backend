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

    @PostMapping
    @PreAuthorize("hasAuthority('CREATOR') or hasAuthority('INSTRUCTOR')")
    public ResponseEntity<ECoupons> createCoupon(
            @RequestParam String creatorId,
            @Valid @RequestBody ECoupons request) {
        return ResponseEntity.ok(couponService.createCoupon(creatorId, request));
    }

    @GetMapping("/creator/{creatorId}")
    @PreAuthorize("hasAuthority('CREATOR') or hasAuthority('INSTRUCTOR')")
    public ResponseEntity<List<ECoupons>> getCouponsByCreator(@PathVariable String creatorId) {
        return ResponseEntity.ok(couponService.getCouponsByCreator(creatorId));
    }

    @PutMapping("/{couponId}")
    @PreAuthorize("hasAuthority('CREATOR') or hasAuthority('INSTRUCTOR')")
    public ResponseEntity<ECoupons> updateCoupon(
            @PathVariable String couponId,
            @RequestParam String creatorId,
            @Valid @RequestBody ECoupons request) {
        return ResponseEntity.ok(couponService.updateCoupon(couponId, creatorId, request));
    }

    @DeleteMapping("/{couponId}")
    @PreAuthorize("hasAuthority('CREATOR') or hasAuthority('INSTRUCTOR')")
    public ResponseEntity<Void> deleteCoupon(
            @PathVariable String couponId,
            @RequestParam String creatorId) {
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
        
        Double discountAmount = couponService.validateCoupon(code, courseId, userId, currentPrice);
        return ResponseEntity.ok(Map.of(
            "valid", discountAmount > 0 || (currentPriceObj != null && discountAmount >= 0),
            "discountAmount", discountAmount
        ));
    }
}
