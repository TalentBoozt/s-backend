package com.talentboozt.s_backend.domains.edu.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_wallet_transactions")
public class EWalletTransaction {
    @Id
    private String id;
    
    @Indexed
    private String userId;
    
    private TransactionType type;
    private Double amount;
    private String currency;
    private TransactionStatus status;
    
    private String referenceId; // original transactionId or payoutId
    private String description;
    
    @CreatedDate
    private Instant createdAt;

    public enum TransactionType {
        SALE, REFUND, COMMISSION, PAYOUT, FUND_RELEASE
    }

    public enum TransactionStatus {
        PENDING, COMPLETED, CANCELLED, FAILED
    }
}
