package com.talentboozt.s_backend.domains.edu.controller;

import com.talentboozt.s_backend.domains.edu.model.EApiKey;
import com.talentboozt.s_backend.domains.edu.service.EduApiKeyService;
import com.talentboozt.s_backend.shared.security.annotations.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/edu/admin/api-keys")
@RequiredArgsConstructor
public class EduApiKeyController {

    private final EduApiKeyService apiKeyService;

    @GetMapping
    public ResponseEntity<List<EApiKey>> getMyKeys(@AuthenticatedUser String userId) {
        return ResponseEntity.ok(apiKeyService.getKeysForOwner(userId));
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createKey(
            @AuthenticatedUser String userId,
            @RequestBody Map<String, Object> body) {
        
        String name = (String) body.get("name");
        List<String> scopes = (List<String>) body.get("scopes");
        
        String rawKey = apiKeyService.generateKey(userId, name, scopes);
        return ResponseEntity.ok(Map.of("apiKey", rawKey));
    }

    @DeleteMapping("/{keyId}")
    public ResponseEntity<Void> revokeKey(
            @AuthenticatedUser String userId,
            @PathVariable String keyId) {
        apiKeyService.revokeKey(keyId, userId);
        return ResponseEntity.ok().build();
    }
}
