package com.talentboozt.s_backend.Model.PLAT_COURSES;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Getter
@Setter

@Document(collection = "course_coupons")
public class CourseCouponsModel {
    @Id
    private String id;

    // Ownership & access
    private String userId;              // User who earned or owns the coupon
    private boolean publicity;          // true = public, false = private
    private String unlockedBy;          // User who unlocked the coupon (if public)
    private String redeemedBy;          // User who redeemed the coupon (if public)

    // Coupon info
    private String code;                // Coupon code (human-readable)
    private String token;               // Secure unique token (e.g., UUID)

    private String discount;            // Discount value (e.g., "10" or "1000")
    private String discountType;        // "percentage" or "amount"

    // Validity & status
    private long validityInMillis;      // Validity duration after unlocking/redeeming
    private Instant createdAt;
    private Instant earnedAt;
    private Instant unlockedAt;
    private Instant redeemedAt;
    private Instant expiresAt;          // Optional: fixed expiration date
    private Status status;              // Enum: CREATED, UNLOCKED, ACTIVE, REDEEMED, EXPIRED

    // Usage scope
    private List<String> applicableCourseIds; // Null or empty = all courses
    private boolean applicableForInstallment; // Can be used for installments

    private String redeemedForCourseId;       // If redeemed
    private String redeemedForInstallmentId;  // If redeemed for an installment

    // Admin & audit
    private String createdBy;           // Admin who created the coupon
    private String taskId;              // Task that granted this coupon
    private String campaignId;          // Gamification campaign ID
    private String tag;                 // Optional categorization/tag
    private String type;                // e.g., "reward", "referral", etc.

    private String activationType; // "auto" or "manual"
    private Instant expiredAt; // Optional: fixed expiration date
    private int maxRedemptions;     // e.g., 100 uses max
    private int currentRedemptions; // track usage

    public enum Status {
        CREATED,
        UNLOCKED,
        ACTIVE,
        REDEEMED,
        EXPIRED,
        LOCKED
    }
}
