package com.talentboozt.s_backend.shared.realtime.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RealtimeBroadcaster {

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastToUser(String userId, String destination, Object payload) {
        messagingTemplate.convertAndSendToUser(userId, destination, payload);
    }

    public void broadcastToTopic(String topic, Object payload) {
        messagingTemplate.convertAndSend("/topic/" + topic, payload);
    }

    public void broadcastToOrganization(String orgId, String subPath, Object payload) {
        messagingTemplate.convertAndSend("/topic/org/" + orgId + "/" + subPath, payload);
    }
}
