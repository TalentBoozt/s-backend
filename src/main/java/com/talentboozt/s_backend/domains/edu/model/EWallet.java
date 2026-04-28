package com.talentboozt.s_backend.domains.edu.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_wallets")
public class EWallet {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String userId;
    
    private Double availableBalance;
    private Double pendingBalance;
    private String currency; // Default USD
    
    @Version
    private Long version;
    
    private Instant updatedAt;
}
