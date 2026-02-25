package com.talentboozt.s_backend.domains.reputation.model;

import lombok.Getter;

@Getter
public enum BadgeType {
    CONTRIBUTOR(500),
    EXPERT(2500),
    AUTHORITY(10000),
    LEGEND(50000);

    private final long threshold;

    BadgeType(long threshold) {
        this.threshold = threshold;
    }
}
