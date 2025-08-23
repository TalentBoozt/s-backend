package com.talentboozt.s_backend.shared.security.service;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RateLimiterService {

    private static final long WINDOW_SIZE_MS = 60_000;

    private final Cache<String, RequestCounter> cache = Caffeine.newBuilder()
            .expireAfterAccess(2, TimeUnit.MINUTES)
            .maximumSize(100_000) // Tune as per expected traffic
            .build();

    public boolean checkRateLimit(String key, String endpointCategory) {
        int limit = switch (endpointCategory) {
            case "analytics" -> 1000;
            case "auth" -> 100;
            case "user" -> 60;
            case "public" -> 200;
            case "coupon-validation" -> 5;
            default -> 300;
        };

        RequestCounter counter = cache.get(key, k -> new RequestCounter());
        return counter.tryAcquire(limit);
    }

    private static class RequestCounter {
        private final AtomicInteger count = new AtomicInteger(0);
        private volatile long windowStart = System.currentTimeMillis();

        public synchronized boolean tryAcquire(int limit) {
            long now = System.currentTimeMillis();
            if (now - windowStart > WINDOW_SIZE_MS) {
                count.set(0);
                windowStart = now;
            }
            return count.incrementAndGet() <= limit;
        }
    }
}
