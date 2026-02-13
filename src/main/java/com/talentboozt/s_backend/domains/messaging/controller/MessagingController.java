package com.talentboozt.s_backend.domains.messaging.controller;

import com.talentboozt.s_backend.domains.messaging.dto.ChatRoomResponse;
import com.talentboozt.s_backend.domains.messaging.dto.MessageResponse;
import com.talentboozt.s_backend.domains.messaging.service.MessagingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/messaging")
@RequiredArgsConstructor
public class MessagingController {
    private final MessagingService messagingService;

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomResponse>> getUserRooms(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(messagingService.getUserRooms(userId));
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<Page<MessageResponse>> getRoomMessages(
            @PathVariable String roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(messagingService.getRoomMessages(roomId, pageable));
    }

    @PostMapping("/rooms/direct/{targetUserId}")
    public ResponseEntity<ChatRoomResponse> getOrCreateDirectRoom(
            @AuthenticationPrincipal String userId,
            @PathVariable String targetUserId) {
        return ResponseEntity.ok(messagingService.getOrCreateEnrichedDirectRoom(userId, targetUserId));
    }

    @PostMapping("/messages/{messageId}/read")
    public ResponseEntity<Void> markAsRead(
            @AuthenticationPrincipal String userId,
            @PathVariable String messageId) {
        messagingService.markAsRead(messageId, userId);
        return ResponseEntity.ok().build();
    }
}
