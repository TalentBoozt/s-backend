package com.talentboozt.s_backend.domains.finance.models;

public interface VersionedEntity {
    Integer getVersion();
    void setVersion(Integer version);
}
