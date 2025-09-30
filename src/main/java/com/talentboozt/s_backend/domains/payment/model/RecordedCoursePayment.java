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
    private String learnerId;
    private String trainerId;

    private BigDecimal amount;        // total paid
    private BigDecimal trainerAmount; // trainer share after split
    private BigDecimal platformAmount;// platform share after split

    private String currency;

    private String paymentMethod;     // stripe, paypal, manual, etc.
    private String paymentStatus;     // pending, paid, refunded, failed
    private String transactionId;     // from Stripe/PayPal/etc.

    private String createdAt;
    private String updatedAt;
}
