package com.talentboozt.s_backend.domains.finance_planning.collaboration.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresenceUpdate {
    private String userId;
    private String userName;
    private String organizationId;
    private String projectId;
    private Cursor cursor;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Cursor {
        private String field;
        private String month;
    }
}
