package com.talentboozt.s_backend.domains.edu.model;

import java.time.Instant;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import com.talentboozt.s_backend.domains.edu.enums.EBeneficiaryType;
import com.talentboozt.s_backend.domains.edu.enums.EHoldingStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_holding_ledger")
public class EHoldingLedger {
    @Id
    private String id;
    
    @Indexed
    private String beneficiaryId;
    
    @Indexed
    private EBeneficiaryType beneficiaryType;
    
    @Indexed
    private String transactionId;
    
    private String courseId;
    
    private Double amount;
    private String currency;
    
    @Indexed
    private EHoldingStatus status;
    
    @Indexed
    private Instant clearanceDate;

    @Version
    private Long version;
    
    @CreatedDate
    private Instant createdAt;
    
    @LastModifiedDate
    private Instant updatedAt;
}
