package com.talentboozt.s_backend.domains.leads.crm.dto;

import lombok.Data;

@Data
public class LLeadUpdateRequest {
    private String status;
    private String note;
}
