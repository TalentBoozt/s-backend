package com.talentboozt.s_backend.domains.payment.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Getter
@Setter
@Document(collection = "recorded_course_payments")
public class RecordedCoursePayment {

    @Id
    private String id;

    private String courseId;
    private String courseName;

    private String learnerId;
    private String trainerId;

    // Payment info
    private BigDecimal grossAmount;       // total amount learner paid (before tax)
    private BigDecimal netAmount;         // after tax & fees
    private String currency;

    // Revenue split (calculated per transaction)
    private BigDecimal trainerAmount;
    private BigDecimal platformAmount;
    private String splitType;             // "trainer-led", "platform-led", "promotion"

    // Transaction info
    private String paymentMethod;         // stripe, paypal, etc.
    private String paymentStatus;         // pending, success, refunded
    private String transactionId;         // from Stripe/PayPal/etc.

    private String createdAt;
    private String updatedAt;
}
