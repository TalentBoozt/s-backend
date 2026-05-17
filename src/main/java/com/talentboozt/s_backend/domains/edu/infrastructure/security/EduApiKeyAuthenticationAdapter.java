package com.talentboozt.s_backend.domains.edu.infrastructure.security;

import com.talentboozt.s_backend.domains.edu.model.EApiKey;
import com.talentboozt.s_backend.domains.edu.service.EduApiKeyService;
import com.talentboozt.s_backend.shared.security.port.ApiKeyAuthenticationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EduApiKeyAuthenticationAdapter implements ApiKeyAuthenticationPort {

    private final EduApiKeyService apiKeyService;

    @Override
    public Optional<ValidatedApiKey> validateActiveKey(String rawKey) {
        EApiKey keyRecord = apiKeyService.validateKey(rawKey);
        if (keyRecord == null || !keyRecord.isActive()) {
            return Optional.empty();
        }
        List<String> scopes = keyRecord.getScopes() != null ? List.copyOf(keyRecord.getScopes()) : Collections.emptyList();
        return Optional.of(new ValidatedApiKey(keyRecord.getOwnerId(), scopes));
    }
}
