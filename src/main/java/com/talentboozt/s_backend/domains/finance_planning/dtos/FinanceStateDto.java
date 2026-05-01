package com.talentboozt.s_backend.domains.finance_planning.dtos;

import com.talentboozt.s_backend.domains.finance_planning.models.*;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class FinanceStateDto {
    private List<FinAssumption> assumptions;
    private List<FinSalesPlan> sales;
    private List<FinPricingModel> pricing;
    private List<FinBudget> budget;
    private List<FinFinancialSnapshot> financials;
    private List<FinScenario> scenarios;
}
