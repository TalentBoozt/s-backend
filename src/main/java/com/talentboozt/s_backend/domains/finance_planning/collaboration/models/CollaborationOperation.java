package com.talentboozt.s_backend.domains.finance_planning.collaboration.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollaborationOperation {
    private String id;
    private OperationType type;
    private String organizationId;
    private String projectId;
    private String path; // e.g., "sales.pro_users"
    private String month; // if applicable
    private Object value;
    private Map<String, Object> payload; // for bulk or complex ops
    private String userId;
    private Long timestamp;
    private Long version;
}
