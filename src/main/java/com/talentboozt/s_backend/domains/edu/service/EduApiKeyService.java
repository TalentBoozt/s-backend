package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.model.EApiKey;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EApiKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EduApiKeyService {

    private final EApiKeyRepository apiKeyRepository;
    private final PasswordEncoder passwordEncoder;
    private static final SecureRandom secureRandom = new SecureRandom();

    public String generateKey(String ownerId, String name, List<String> scopes) {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        String rawKey = "tn_" + Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        
        EApiKey apiKey = EApiKey.builder()
                .apiKey(passwordEncoder.encode(rawKey))
                .apiKeyHint(rawKey.substring(0, 7) + "..." + rawKey.substring(rawKey.length() - 4))
                .ownerId(ownerId)
                .name(name)
                .isActive(true)
                .scopes(scopes)
                .createdAt(Instant.now())
                .build();
        
        apiKeyRepository.save(apiKey);
        return rawKey;
    }

    public List<EApiKey> getKeysForOwner(String ownerId) {
        return apiKeyRepository.findByOwnerId(ownerId);
    }

    public void revokeKey(String keyId, String ownerId) {
        EApiKey key = apiKeyRepository.findById(keyId)
                .orElseThrow(() -> new RuntimeException("API Key not found"));
        
        if (key.getOwnerId().equals(ownerId)) {
            apiKeyRepository.delete(key);
        }
    }

    public EApiKey validateKey(String rawKey) {
        // This is inefficient if many keys exist, but fits simplified storage.
        // In production, split key into ID + Secret for O(1) lookup.
        return apiKeyRepository.findAll().stream()
                .filter(k -> passwordEncoder.matches(rawKey, k.getApiKey()))
                .findFirst()
                .orElse(null);
    }
}
