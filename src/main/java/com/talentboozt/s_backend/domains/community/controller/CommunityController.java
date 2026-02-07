package com.talentboozt.s_backend.domains.community.controller;

import com.talentboozt.s_backend.domains.community.dto.CommunityDTO;
import com.talentboozt.s_backend.domains.community.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<CommunityDTO> getCommunityById(@PathVariable String id) {
        return ResponseEntity.ok(communityService.getCommunityById(id));
    }

    @PostMapping
    public ResponseEntity<CommunityDTO> createCommunity(@RequestBody CommunityDTO communityDTO) {
        return new ResponseEntity<>(communityService.createCommunity(communityDTO), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommunity(@PathVariable String id) {
        communityService.deleteCommunity(id);
        return ResponseEntity.noContent().build();
    }
}

