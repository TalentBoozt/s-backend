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
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(messagingService.getRoomMessages(roomId, userDetails.getUserId(), pageable));
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

    @PostMapping("/keys")
    public ResponseEntity<Void> setPublicKey(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody String publicKey) {
        messagingService.setPublicKey(userDetails.getUserId(), publicKey);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/keys/user/{userId}")
    public ResponseEntity<String> getPublicKey(@PathVariable String userId) {
        return ResponseEntity.ok(messagingService.getPublicKey(userId));
    }

    @GetMapping("/keys/room/{roomId}")
    public ResponseEntity<java.util.Map<String, String>> getRoomPublicKeys(@PathVariable String roomId) {
        return ResponseEntity.ok(messagingService.getRoomPublicKeys(roomId));
    }

    @PostMapping("/messages/{messageId}/forward")
    public ResponseEntity<Void> forwardMessage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String messageId,
            @RequestBody List<String> targetRoomIds) {
        messagingService.forwardMessage(messageId, targetRoomIds, userDetails.getUserId());
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/rooms/{roomId}/pin")
    public ResponseEntity<Void> pinRoom(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String roomId,
            @RequestParam boolean pin) {
        messagingService.pinRoom(roomId, userDetails.getUserId(), pin);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rooms/{roomId}/archive")
    public ResponseEntity<Void> archiveRoom(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String roomId,
            @RequestParam boolean archive) {
        messagingService.archiveRoom(roomId, userDetails.getUserId(), archive);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rooms/{roomId}/favorite")
    public ResponseEntity<Void> favoriteRoom(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String roomId,
            @RequestParam boolean favorite) {
        messagingService.favoriteRoom(roomId, userDetails.getUserId(), favorite);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/rooms/{roomId}")
    public ResponseEntity<Void> deleteRoom(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String roomId) {
        messagingService.deleteRoom(roomId, userDetails.getUserId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rooms/{roomId}/read-all")
    public ResponseEntity<Void> markRoomAsRead(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String roomId) {
        messagingService.markRoomAsRead(roomId, userDetails.getUserId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rooms/{roomId}/exit")
    public ResponseEntity<Void> exitGroup(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String roomId) {
        messagingService.exitGroup(roomId, userDetails.getUserId());
        return ResponseEntity.ok().build();
    }
}
