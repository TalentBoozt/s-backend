package com.talentboozt.s_backend.shared.security.port;

import java.util.List;
import java.util.Optional;

/**
 * Validates API keys without coupling security filters to LMS persistence types.
 */
public interface ApiKeyAuthenticationPort {

    Optional<ValidatedApiKey> validateActiveKey(String rawKey);

    record ValidatedApiKey(String ownerId, List<String> scopes) {
    }
}
