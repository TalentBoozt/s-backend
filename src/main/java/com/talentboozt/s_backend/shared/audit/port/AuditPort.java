package com.talentboozt.s_backend.shared.audit.port;

import java.util.Map;

/**
 * Port interface for domain-agnostic audit logging.
 * Products use this to emit audit events without depending
 * on the audit infrastructure directly.
 */
public interface AuditPort {

    /**
     * Log an audit event.
     * 
     * @param eventType the type of event (e.g., "USER_REGISTERED", "COURSE_ENROLLED")
     * @param actorId   the user who performed the action
     * @param targetId  the target entity ID
     * @param metadata  additional event metadata
     */
    void logEvent(String eventType, String actorId, String targetId, Map<String, Object> metadata);
}
