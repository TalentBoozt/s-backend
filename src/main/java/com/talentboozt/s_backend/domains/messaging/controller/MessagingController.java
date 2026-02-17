package com.talentboozt.s_backend.domains.messaging.controller;

import com.talentboozt.s_backend.domains.messaging.dto.ChatRoomResponse;
import com.talentboozt.s_backend.domains.messaging.dto.MessageResponse;
import com.talentboozt.s_backend.domains.messaging.service.MessagingService;
import com.talentboozt.s_backend.shared.security.model.CustomUserDetails;
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
    public ResponseEntity<List<ChatRoomResponse>> getUserRooms(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            return ResponseEntity.ok(messagingService.getUserRooms(userDetails.getUserId()));
        }
        return ResponseEntity.ok().build();
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
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String targetUserId) {
        if (userDetails != null) {
            return ResponseEntity.ok(messagingService.getOrCreateEnrichedDirectRoom(userDetails.getUserId(), targetUserId));
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomResponse> createGroupRoom(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody com.talentboozt.s_backend.domains.messaging.dto.CreateRoomRequest request) {
        return ResponseEntity.ok(messagingService.createGroupRoom(userDetails.getUserId(), request));
    }

    @PostMapping("/messages/{messageId}/read")
    public ResponseEntity<Void> markAsRead(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String messageId) {
        if (userDetails != null) {
            messagingService.markAsRead(messageId, userDetails.getUserId());
        }
        return ResponseEntity.ok().build();
    }
}
