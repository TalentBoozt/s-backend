package com.talentboozt.s_backend.domains.edu.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.talentboozt.s_backend.domains.edu.enums.EPaymentMethod;
import com.talentboozt.s_backend.domains.edu.enums.EPaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_transactions")
public class ETransactions {
    @Id
    private String id;
    
    @Indexed
    private String courseId;
    
    @Indexed
    private String buyerId;
    
    @Indexed
    private String sellerId;
    
    private Double amount;
    private String currency; // "USD"
    private Double platformFee;
    private Double creatorEarning;
    
    @Indexed
    private EPaymentMethod paymentMethod;
    
    @Indexed
    private EPaymentStatus paymentStatus;
    
    @Indexed(unique = true)
    private String transactionId;

    /** Stripe Checkout Session id for one-time course purchases (idempotency). */
    @Indexed
    private String stripeCheckoutSessionId;
    
    private String paymentGateway;
    private String paymentGatewayResponse;
    private String createdBy;
    private String updatedBy;
    
    @CreatedDate
    private Instant createdAt;
    
    @LastModifiedDate
    private Instant updatedAt;
}
