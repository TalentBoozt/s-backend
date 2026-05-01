package com.talentboozt.s_backend.domains.finance_planning.dtos;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class BulkUpdateDto {
    private List<BulkOperation> operations;

    @Data
    public static class BulkOperation {
        private String type; // assumption, sales, budget, pricing
        private String action; // create, update, delete
        private String id;
        private Integer expectedVersion;
        private Map<String, Object> payload;
    }
}
