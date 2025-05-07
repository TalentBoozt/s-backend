package com.talentboozt.s_backend.Service._private;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RateLimiterService {

    private final ConcurrentHashMap<String, RequestCounter> requestCounters = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS_PER_MINUTE = 100;

    public boolean checkRateLimit(String ipAddress) {
        long currentTimeMillis = System.currentTimeMillis();
        RequestCounter counter = requestCounters.computeIfAbsent(ipAddress, k -> new RequestCounter());

        // Clean up old requests that are outside of the time window
        counter.cleanup(currentTimeMillis);

        if (counter.getCount() >= MAX_REQUESTS_PER_MINUTE) {
            return false; // Too many requests
        }

        counter.increment();
        return true;
    }

    private static class RequestCounter {
        private static final long WINDOW_SIZE = 60000; // 1 minute window
        private final AtomicInteger count = new AtomicInteger(0);
        private long lastRequestTime = System.currentTimeMillis();

        public void increment() {
            count.incrementAndGet();
        }

        public int getCount() {
            return count.get();
        }

        public void cleanup(long currentTimeMillis) {
            if (currentTimeMillis - lastRequestTime > WINDOW_SIZE) {
                count.set(0); // Reset count after 1 minute window
            }
            lastRequestTime = currentTimeMillis;
        }
    }
}
