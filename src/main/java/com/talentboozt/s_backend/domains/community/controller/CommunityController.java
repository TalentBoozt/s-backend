package com.talentboozt.s_backend.domains.community.controller;

import com.talentboozt.s_backend.domains.community.dto.CommunityDTO;
import com.talentboozt.s_backend.domains.community.model.CommunityMember;
import com.talentboozt.s_backend.domains.community.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/communities")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    @GetMapping
    public ResponseEntity<List<CommunityDTO>> getAllCommunities() {
        return ResponseEntity.ok(communityService.getAllCommunities());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommunityDTO> getCommunityById(@PathVariable String id,
            @RequestParam(required = false) String userId,
            java.security.Principal principal) {
        String currentUserId = userId;
        if (currentUserId == null && principal != null) {
            currentUserId = principal.getName();
        }
        return ResponseEntity.ok(communityService.getCommunityById(id, currentUserId));
    }

    @PostMapping
    public ResponseEntity<CommunityDTO> createCommunity(@RequestBody CommunityDTO communityDTO) {
        return new ResponseEntity<>(communityService.createCommunity(communityDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommunityDTO> updateCommunity(@PathVariable String id,
            @RequestBody CommunityDTO communityDTO) {
        return ResponseEntity.ok(communityService.updateCommunity(id, communityDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommunity(@PathVariable String id) {
        communityService.deleteCommunity(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<CommunityDTO> joinCommunity(@PathVariable String id, @RequestParam String userId) {
        return ResponseEntity.ok(communityService.joinCommunity(id, userId));
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<CommunityDTO> leaveCommunity(@PathVariable String id, @RequestParam String userId) {
        return ResponseEntity.ok(communityService.leaveCommunity(id, userId));
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<CommunityMember>> getCommunityMembers(@PathVariable String id) {
        return ResponseEntity.ok(communityService.getCommunityMembers(id));
    }

    @PostMapping("/{communityId}/members/{userId}/role")
    public ResponseEntity<CommunityMember> updateMemberRole(
            @PathVariable String communityId,
            @PathVariable String userId,
            @RequestBody Map<String, String> payload) {
        String role = payload.get("role");
        return ResponseEntity.ok(communityService.updateMemberRole(communityId, userId, role));
    }

    @DeleteMapping("/{communityId}/members/{userId}")
    public ResponseEntity<Void> removeMember(@PathVariable String communityId, @PathVariable String userId) {
        communityService.removeMember(communityId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CommunityDTO>> getUserCommunities(@PathVariable String userId) {
        return ResponseEntity.ok(communityService.getUserCommunities(userId));
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<Map<String, Object>> getCommunityStats(@PathVariable String id) {
        return ResponseEntity.ok(communityService.getCommunityStats(id));
    }

    @PostMapping("/{communityId}/members/{userId}/ban")
    public ResponseEntity<Void> banMember(
            @PathVariable String communityId,
            @PathVariable String userId,
            @RequestBody(required = false) Map<String, String> payload) {
        String reason = payload != null ? payload.get("reason") : null;
        communityService.banMember(communityId, userId, reason);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{communityId}/members/{userId}/unban")
    public ResponseEntity<Void> unbanMember(@PathVariable String communityId, @PathVariable String userId) {
        communityService.unbanMember(communityId, userId);
        return ResponseEntity.ok().build();
    }
}
