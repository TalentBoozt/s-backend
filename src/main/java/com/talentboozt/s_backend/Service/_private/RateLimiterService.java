package com.talentboozt.s_backend.Service._private;
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
            default -> 300;
        };

        long now = System.currentTimeMillis();
        RequestCounter counter = cache.get(key, k -> new RequestCounter());
        counter.cleanup(now);

        if (counter.getCount() >= limit) {
            return false;
        }

        counter.increment(now);
        return true;
    }

    private static class RequestCounter {
        private final Queue<Long> timestamps = new ConcurrentLinkedQueue<>();

        public void increment(long now) {
            timestamps.add(now);
        }

        public int getCount() {
            return timestamps.size();
        }

        public void cleanup(long now) {
            while (!timestamps.isEmpty() && (now - timestamps.peek() > WINDOW_SIZE_MS)) {
                timestamps.poll();
            }
        }
    }
}
