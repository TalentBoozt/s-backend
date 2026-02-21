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
        return ResponseEntity.ok(messagingService.getUserRooms(userDetails.getUserId()));
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
        return ResponseEntity.ok(messagingService.getOrCreateEnrichedDirectRoom(userDetails.getUserId(), targetUserId));
    }

    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomResponse> createGroupRoom(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody com.talentboozt.s_backend.domains.messaging.dto.CreateRoomRequest request) {
        return ResponseEntity.ok(messagingService.createGroupRoom(userDetails.getUserId(), request));
    }

    @PostMapping("/messages/{messageId}/react")
    public ResponseEntity<MessageResponse> reactToMessage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String messageId,
            @RequestParam String emoji) {
        return ResponseEntity.ok(messagingService.reactToMessage(messageId, userDetails.getUserId(), emoji));
    }

    @PutMapping("/messages/{messageId}")
    public ResponseEntity<MessageResponse> editMessage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String messageId,
            @RequestBody String content) {
        return ResponseEntity.ok(messagingService.editMessage(messageId, userDetails.getUserId(), content));
    }

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String messageId,
            @RequestParam(defaultValue = "false") boolean forEveryone) {
        messagingService.deleteMessage(messageId, userDetails.getUserId(), forEveryone);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rooms/{roomId}/pin/{messageId}")
    public ResponseEntity<Void> pinMessage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String roomId,
            @PathVariable String messageId,
            @RequestParam boolean pin) {
        messagingService.pinMessage(roomId, messageId, pin);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/messages/{messageId}/read")
    public ResponseEntity<Void> markAsRead(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String messageId) {
        messagingService.markAsRead(messageId, userDetails.getUserId());
        return ResponseEntity.ok().build();
    }
}
