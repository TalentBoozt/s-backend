package com.talentboozt.s_backend.domains.edu.model;

import java.time.Instant;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import com.talentboozt.s_backend.domains.edu.enums.ECreditLedgerActionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_credit_ledger")
public class ECreditLedger {
    @Id
    private String id;
    
    @Indexed
    private String userId;
    
    private ECreditLedgerActionType actionType; // GRANT, DEDUCT, EXPIRE, REFUND
    private Integer amount;
    
    private Integer balanceBefore;
    private Integer balanceAfter; // Mandatory snapshot for EDU-701
    private Integer newBalance; // Alias for balanceAfter to match service usage
    
    private String metadata;
    private String referenceId; // Transaction ID or AI Request ID
    private String referenceType; // EAIUsageType or similar
    
    @CreatedDate
    @Indexed
    private Instant createdAt;
}
