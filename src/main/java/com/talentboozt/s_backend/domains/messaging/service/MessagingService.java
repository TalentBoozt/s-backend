package com.talentboozt.s_backend.domains.messaging.service;

import com.talentboozt.s_backend.domains.messaging.dto.*;
import com.talentboozt.s_backend.domains.messaging.model.*;
import com.talentboozt.s_backend.domains.messaging.repository.mongodb.ChatRoomRepository;
import com.talentboozt.s_backend.domains.messaging.repository.mongodb.MessageRepository;
import com.talentboozt.s_backend.domains.user.model.EmployeeModel;
import com.talentboozt.s_backend.domains.user.repository.mongodb.EmployeeRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessagingService {
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final EmployeeRepository employeeRepository;
    private final PresenceService presenceService;

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

    public List<ChatRoomResponse> getUserRooms(String userId) {
        return chatRoomRepository.findByParticipantsContaining(userId).stream()
                .map(room -> enrichRoom(room, userId))
                .collect(Collectors.toList());
    }

    public Page<MessageResponse> getRoomMessages(String roomId, Pageable pageable) {
        return messageRepository.findByRoomId(roomId, pageable)
                .map(this::mapToResponse);
    }

    public ChatRoomResponse getOrCreateEnrichedDirectRoom(String userId, String targetUserId) {
        ChatRoom room = getOrCreateDirectRoom(userId, targetUserId);
        return enrichRoom(room, userId);
    }

    public void markAsRead(String messageId, String userId) {
        messageRepository.findById(messageId).ifPresent(message -> {
            if (message.getReadByUsers() == null) {
                message.setReadByUsers(new HashMap<>());
            }
            message.getReadByUsers().put(userId, LocalDateTime.now());
            messageRepository.save(message);

            // Notify sender or room about read status
            messagingTemplate.convertAndSend("/topic/room/" + message.getRoomId() + "/read",
                    java.util.Map.of("messageId", messageId, "userId", userId));
        });
    }

    private ChatRoomResponse enrichRoom(ChatRoom room, String currentUserId) {
        List<ParticipantDTO> participants = room.getParticipants().stream()
                .map(userId -> {
                    EmployeeModel employee = employeeRepository.findById(userId).orElse(null);
                    PresenceStatus status = presenceService.getUserPresence(userId).getStatus();
                    String name = "Unknown User";
                    String avatar = null;

                    if (employee != null) {
                        name = employee.getFirstname() + " " + employee.getLastname();
                        avatar = employee.getImage();
                    }

                    return ParticipantDTO.builder()
                            .userId(userId)
                            .name(name)
                            .avatar(avatar)
                            .status(status)
                            .build();
                })
                .collect(Collectors.toList());

        Message lastMessage = messageRepository.findFirstByRoomIdOrderByCreatedAtDesc(room.getId()).orElse(null);
        long unreadCount = messageRepository.countUnreadMessages(room.getId(), currentUserId, currentUserId);

        return ChatRoomResponse.builder()
                .id(room.getId())
                .type(room.getType())
                .participants(participants)
                .communityId(room.getCommunityId())
                .lastMessage(lastMessage != null ? mapToResponse(lastMessage) : null)
                .unreadCount((int) unreadCount)
                .createdAt(room.getCreatedAt())
                .build();
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
