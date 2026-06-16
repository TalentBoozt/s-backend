package com.talentboozt.s_backend.shared.audit.tracking.dto.monitor;

import lombok.Data;
import java.util.List;

@Data
public class ExperimentResultsDTO {
    private String experimentId;
    private List<ExperimentVariantCount> assignments;
    private List<ExperimentConversionStats> conversions;
    private Double conversionRate;
}