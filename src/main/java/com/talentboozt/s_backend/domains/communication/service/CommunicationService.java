package com.talentboozt.s_backend.domains.communication.service;

import com.talentboozt.s_backend.domains.communication.model.CommunicationMessageModel;
import com.talentboozt.s_backend.domains.communication.repository.mongodb.CommunicationMessageRepository;
import com.talentboozt.s_backend.shared.realtime.service.RealtimeBroadcaster;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommunicationService {
    private final CommunicationMessageRepository messageRepository;
    private final RealtimeBroadcaster realtimeBroadcaster;

    public CommunicationMessageModel sendMessage(CommunicationMessageModel message) {
        message.setTimestamp(Instant.now());
        message.setRead(false);
        
        CommunicationMessageModel saved = messageRepository.save(message);
        
        // Broadcast to receiver
        realtimeBroadcaster.broadcastToUser(message.getReceiverId(), "/queue/messages", saved);
        
        return saved;
    }

    public void sendTypingIndicator(String threadId, String senderId, String receiverId, boolean isTyping) {
        realtimeBroadcaster.broadcastToUser(receiverId, "/queue/typing", Map.of(
            "threadId", threadId,
            "senderId", senderId,
            "isTyping", isTyping
        ));
    }
}
