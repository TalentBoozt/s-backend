package com.talentboozt.s_backend.domains.finance_planning.models;

public interface VersionedEntity {
    Integer getVersion();
    void setVersion(Integer version);
}
