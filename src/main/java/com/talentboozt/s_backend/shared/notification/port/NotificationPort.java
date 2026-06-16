package com.talentboozt.s_backend.shared.notification.port;

import java.util.Map;

/**
 * Port interface for sending notifications.
 * Products use this to send notifications without depending
 * on the notification infrastructure directly.
 */
public interface NotificationPort {

    /**
     * Send a notification to a specific user.
     */
    void sendToUser(String userId, String title, String message, Map<String, Object> metadata);

    /**
     * Broadcast a notification to all users.
     */
    void broadcast(String title, String message, Map<String, Object> metadata);
}
