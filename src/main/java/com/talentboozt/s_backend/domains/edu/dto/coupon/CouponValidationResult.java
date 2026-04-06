package com.talentboozt.s_backend.domains.edu.dto.coupon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Structured result from coupon validation.
 * Eliminates the ambiguity of returning just a number from validateCoupon().
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponValidationResult {
    /** The coupon's database ID */
    private String couponId;
    /** The coupon code */
    private String code;
    /** Original price before discount */
    private double originalPrice;
    /** The discount amount */
    private double discountAmount;
    /** Final price after discount */
    private double finalPrice;
    /** Discount type (PERCENTAGE or FLAT) */
    private String discountType;
    /** Discount value (percentage or flat amount) */
    private double discountValue;
}
