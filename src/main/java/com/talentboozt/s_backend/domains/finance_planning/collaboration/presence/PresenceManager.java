package com.talentboozt.s_backend.domains.finance_planning.collaboration.presence;

import com.talentboozt.s_backend.domains.finance_planning.collaboration.models.PresenceUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PresenceManager {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String PRESENCE_KEY_PREFIX = "finance:presence:";
    private static final long PRESENCE_TTL = 30; // seconds

    public void updatePresence(PresenceUpdate update) {
        String key = PRESENCE_KEY_PREFIX + update.getProjectId();
        String userKey = update.getUserId();
        
        redisTemplate.opsForHash().put(key, userKey, update);
        redisTemplate.expire(key, PRESENCE_TTL, TimeUnit.SECONDS);
    }

    public List<PresenceUpdate> getActiveUsers(String projectId) {
        String key = PRESENCE_KEY_PREFIX + projectId;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        List<PresenceUpdate> users = new ArrayList<>();
        for (Object value : entries.values()) {
            if (value instanceof PresenceUpdate) {
                users.add((PresenceUpdate) value);
            }
        }
        return users;
    }

    public void removeUser(String projectId, String userId) {
        String key = PRESENCE_KEY_PREFIX + projectId;
        redisTemplate.opsForHash().delete(key, userId);
    }
}
