package com.talentboozt.s_backend.domains.messaging.service;

import com.talentboozt.s_backend.domains.messaging.model.PresenceStatus;
import com.talentboozt.s_backend.domains.messaging.model.UserPresence;
import com.talentboozt.s_backend.domains.messaging.repository.redis.UserPresenceRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PresenceService {
    private final UserPresenceRepository userPresenceRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void updateUserStatus(String userId, PresenceStatus status) {
        UserPresence presence = UserPresence.builder()
                .userId(userId)
                .status(status)
                .lastSeen(LocalDateTime.now())
                .build();

        userPresenceRepository.save(presence);

        // Broadcast presence update
        messagingTemplate.convertAndSend("/topic/presence/" + userId, presence);
    }

    public UserPresence getUserPresence(String userId) {
        return userPresenceRepository.findById(userId)
                .orElse(UserPresence.builder()
                        .userId(userId)
                        .status(PresenceStatus.OFFLINE)
                        .lastSeen(LocalDateTime.now())
                        .build());
    }
}
