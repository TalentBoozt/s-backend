package com.talentboozt.s_backend.domains.edu.dto.finance;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RevenueSummaryDTO {
    private String creatorId;
    private Double totalEarnings;
    private Double pendingBalance;
    private Double availableBalance;
    private Double pendingClearance;
    private Double withdrawnAmount;
    private Integer totalTransactions;
}
