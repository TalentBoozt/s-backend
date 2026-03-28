package com.talentboozt.s_backend.domains.edu.enums;

public enum ECourseStatus {
    DRAFT,
    /** Submitted by creator; hidden from marketplace until admin approves. */
    PENDING_REVIEW,
    PUBLISHED,
    ARCHIVED,
    SUSPENDED
}
