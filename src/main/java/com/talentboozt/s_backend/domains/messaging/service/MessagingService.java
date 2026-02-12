package com.talentboozt.s_backend.domains.messaging.service;

import com.talentboozt.s_backend.domains.messaging.dto.MessageRequest;
import com.talentboozt.s_backend.domains.messaging.dto.MessageResponse;
import com.talentboozt.s_backend.domains.messaging.model.*;
import com.talentboozt.s_backend.domains.messaging.repository.mongodb.ChatRoomRepository;
import com.talentboozt.s_backend.domains.messaging.repository.mongodb.MessageRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessagingService {
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatRoom getOrCreateDirectRoom(String user1, String user2) {
        List<String> participants = List.of(user1, user2);
        // Simplified check
        return chatRoomRepository.findByParticipantsContaining(user1).stream()
                .filter(room -> room.getType() == RoomType.DIRECT && room.getParticipants().contains(user2))
                .findFirst()
                .orElseGet(() -> chatRoomRepository.save(ChatRoom.builder()
                        .type(RoomType.DIRECT)
                        .participants(participants)
                        .createdAt(LocalDateTime.now())
                        .build()));
    }

    public MessageResponse sendMessage(String senderId, MessageRequest request) {
        Message message = Message.builder()
                .roomId(request.getRoomId())
                .senderId(senderId)
                .content(request.getContent())
                .messageType(request.getMessageType() != null ? request.getMessageType() : MessageType.TEXT)
                .createdAt(LocalDateTime.now())
                .readByUsers(new HashMap<>())
                .build();

        Message saved = messageRepository.save(message);
        MessageResponse response = mapToResponse(saved);

        // Broadcast to room
        messagingTemplate.convertAndSend("/topic/room/" + request.getRoomId(), response);

        return response;
    }

    private MessageResponse mapToResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .roomId(message.getRoomId())
                .senderId(message.getSenderId())
                .content(message.getContent())
                .messageType(message.getMessageType())
                .createdAt(message.getCreatedAt())
                .readByUsers(message.getReadByUsers())
                .build();
    }
}
