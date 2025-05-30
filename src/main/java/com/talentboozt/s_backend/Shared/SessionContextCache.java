package com.talentboozt.s_backend.Shared;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class SessionContextCache {

    private final Cache<String, SessionContext> cache;

    public SessionContextCache() {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(50_000)
                .build();
    }

    public void store(String sessionId, SessionContext context) {
        cache.put(sessionId, context);
    }

    public SessionContext get(String sessionId) {
        return cache.getIfPresent(sessionId);
    }

    public void remove(String sessionId) {
        cache.invalidate(sessionId);
    }

    public void clear() {
        cache.invalidateAll();
    }
}
