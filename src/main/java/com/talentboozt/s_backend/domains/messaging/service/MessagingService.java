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
import org.springframework.data.redis.RedisConnectionFailureException;
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
                .metadata(request.getMetadata())
                .isEncrypted(request.isEncrypted())
                .expiresAt(request.getExpiresAt())
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

    public Page<MessageResponse> getRoomMessages(String roomId, String userId, Pageable pageable) {
        return messageRepository.findByRoomIdAndDeletedForUsersNotContaining(roomId, userId, pageable)
                .map(this::mapToResponse);
    }

    public ChatRoomResponse getOrCreateEnrichedDirectRoom(String userId, String targetUserId) {
        ChatRoom room = getOrCreateDirectRoom(userId, targetUserId);
        return enrichRoom(room, userId);
    }

    public ChatRoomResponse createGroupRoom(String creatorId, CreateRoomRequest request) {
        List<String> participants = new java.util.ArrayList<>(request.getParticipants());
        if (!participants.contains(creatorId)) {
            participants.add(creatorId);
        }

        ChatRoom room = ChatRoom.builder()
                .type(RoomType.GROUP)
                .name(request.getName())
                .participants(participants)
                .communityId(request.getCommunityId())
                .createdAt(LocalDateTime.now())
                .build();

        ChatRoom saved = chatRoomRepository.save(room);
        return enrichRoom(saved, creatorId);
    }

    public void markAsRead(String messageId, String userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        if (message.getReadByUsers() == null) {
            message.setReadByUsers(new java.util.HashMap<>());
        }
        message.getReadByUsers().put(userId, LocalDateTime.now());
        messageRepository.save(message);

        // Notify sender or room about read status
        messagingTemplate.convertAndSend("/topic/room/" + message.getRoomId() + "/read",
                java.util.Map.of("messageId", messageId, "userId", userId));
    }

    public MessageResponse reactToMessage(String messageId, String userId, String emoji) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if (message.getReactions() == null) {
            message.setReactions(new java.util.HashMap<>());
        }

        List<String> users = message.getReactions().computeIfAbsent(emoji, k -> new java.util.ArrayList<>());
        if (users.contains(userId)) {
            users.remove(userId);
            if (users.isEmpty()) {
                message.getReactions().remove(emoji);
            }
        } else {
            users.add(userId);
        }

        MessageResponse response = mapToResponse(messageRepository.save(message));
        messagingTemplate.convertAndSend("/topic/room/" + message.getRoomId() + "/update", response);
        return response;
    }

    public MessageResponse editMessage(String messageId, String userId, String newContent) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if (!message.getSenderId().equals(userId)) {
            throw new RuntimeException("Not authorized to edit this message");
        }

        message.setContent(newContent);
        message.setEdited(true);
        message.setUpdatedAt(LocalDateTime.now());

        MessageResponse response = mapToResponse(messageRepository.save(message));
        messagingTemplate.convertAndSend("/topic/room/" + message.getRoomId() + "/update", response);
        return response;
    }

    public void deleteMessage(String messageId, String userId, boolean forEveryone) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if (forEveryone) {
            if (!message.getSenderId().equals(userId)) {
                throw new RuntimeException("Not authorized to delete for everyone");
            }
            message.setDeleted(true);
            message.setContent("This message was deleted");
            messageRepository.save(message);

            MessageResponse response = mapToResponse(message);
            messagingTemplate.convertAndSend("/topic/room/" + message.getRoomId() + "/update", response);
        } else {
            if (message.getDeletedForUsers() == null) {
                message.setDeletedForUsers(new java.util.ArrayList<>());
            }
            if (!message.getDeletedForUsers().contains(userId)) {
                message.getDeletedForUsers().add(userId);
                messageRepository.save(message);
            }
        }
    }

    public void pinMessage(String roomId, String messageId, boolean pin) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if (!message.getRoomId().equals(roomId)) {
            throw new RuntimeException("Message does not belong to this room");
        }

        message.setPinned(pin);
        messageRepository.save(message);

        MessageResponse response = mapToResponse(message);
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/pin", response);
    }

    public void setPublicKey(String userId, String publicKey) {
        EmployeeModel employee = employeeRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        employee.setMessagingPublicKey(publicKey);
        employeeRepository.save(employee);
    }

    public String getPublicKey(String userId) {
        return employeeRepository.findById(userId)
                .map(EmployeeModel::getMessagingPublicKey)
                .orElse(null);
    }

    public java.util.Map<String, String> getRoomPublicKeys(String roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        java.util.Map<String, String> keys = new java.util.HashMap<>();
        for (String userId : room.getParticipants()) {
            employeeRepository.findById(userId)
                    .map(EmployeeModel::getMessagingPublicKey)
                    .ifPresent(key -> keys.put(userId, key));
        }
        return keys;
    }

    public void forwardMessage(String messageId, List<String> targetRoomIds, String userId) {
        Message originalMessage = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        for (String roomId : targetRoomIds) {
            Message forwardedMessage = Message.builder()
                    .roomId(roomId)
                    .senderId(userId)
                    .content(originalMessage.getContent())
                    .messageType(originalMessage.getMessageType())
                    .createdAt(LocalDateTime.now())
                    .isForwarded(true)
                    .forwardedFromId(originalMessage.getId())
                    .build();

            Message saved = messageRepository.save(forwardedMessage);
            messagingTemplate.convertAndSend("/topic/room/" + roomId, mapToResponse(saved));
        }
    }

    private ChatRoomResponse enrichRoom(ChatRoom room, String currentUserId) {
        List<ParticipantDTO> participants = room.getParticipants().stream()
                .map(userId -> {
                    EmployeeModel employee = employeeRepository.findById(userId).orElse(null);
                    PresenceStatus status;
                    try {
                        status = presenceService.getUserPresence(userId).getStatus();
                    } catch (RedisConnectionFailureException e) {
                        status = PresenceStatus.OFFLINE;
                    }
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

        Message lastMessage = messageRepository.findLatestActiveMessage(room.getId(), currentUserId).orElse(null);
        Long unreadCount = messageRepository.countUnreadMessages(room.getId(), currentUserId, currentUserId);
        long safeUnread = unreadCount != null ? unreadCount : 0L;

        return ChatRoomResponse.builder()
                .id(room.getId())
                .type(room.getType())
                .name(room.getName())
                .participants(participants)
                .communityId(room.getCommunityId())
                .lastMessage(lastMessage != null ? mapToResponse(lastMessage) : null)
                .unreadCount((int) safeUnread)
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
                .isEdited(message.isEdited())
                .updatedAt(message.getUpdatedAt())
                .isDeleted(message.isDeleted())
                .deletedForUsers(message.getDeletedForUsers())
                .reactions(message.getReactions())
                .metadata(message.getMetadata())
                .isForwarded(message.isForwarded())
                .forwardedFromId(message.getForwardedFromId())
                .isPinned(message.isPinned())
                .expiresAt(message.getExpiresAt())
                .isEncrypted(message.isEncrypted())
                .build();
    }
}
