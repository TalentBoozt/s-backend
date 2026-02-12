package com.talentboozt.s_backend.domains.reputation.model;

import lombok.Getter;

@Getter
public enum BadgeType {
    CONTRIBUTOR(100),
    EXPERT(500),
    AUTHORITY(2000),
    LEGEND(10000);

    private final long threshold;

    BadgeType(long threshold) {
        this.threshold = threshold;
    }
}
