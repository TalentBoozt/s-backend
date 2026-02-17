package com.talentboozt.s_backend.domains.user.model;

/**
 * Platform-level roles for RBAC
 * Used to control access to platform-wide features like moderation and
 * announcements
 */
public enum PlatformRole {
    /**
     * Platform administrator - full access to all features
     */
    PLATFORM_ADMIN,

    /**
     * Platform moderator - access to moderation dashboard and content management
     */
    PLATFORM_MODERATOR,

    /**
     * Regular user - standard platform access
     */
    USER
}
