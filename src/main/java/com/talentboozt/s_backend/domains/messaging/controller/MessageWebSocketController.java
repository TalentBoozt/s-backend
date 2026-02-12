package com.talentboozt.s_backend.domains.messaging.controller;

import com.talentboozt.s_backend.domains.messaging.dto.MessageRequest;
import com.talentboozt.s_backend.domains.messaging.service.MessagingService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {
    private final MessagingService messagingService;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload MessageRequest request, SimpMessageHeaderAccessor headerAccessor) {
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
        if (userId != null) {
            messagingService.sendMessage(userId, request);
        }
    }

    @MessageMapping("/chat.typing")
    public void typing(@Payload TypingEvent event, SimpMessageHeaderAccessor headerAccessor) {
        // Broadcast typing status to room
        // messagingTemplate.convertAndSend("/topic/room/" + event.getRoomId() +
        // "/typing", event);
        // This would be handled in a more complete implementation
    }

    public record TypingEvent(String roomId, String userId, boolean typing) {
    }
}
