package com.talentboozt.s_backend.domains.activity.service;

import com.talentboozt.s_backend.domains.activity.model.JPActivityModel;
import com.talentboozt.s_backend.domains.activity.repository.mongodb.JPActivityRepository;
import com.talentboozt.s_backend.shared.realtime.service.RealtimeBroadcaster;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JPActivityService {
    private final JPActivityRepository activityRepository;
    private final RealtimeBroadcaster realtimeBroadcaster;

    public JPActivityModel logActivity(String userId, String orgId, String type, String title, String content, String domain, Map<String, Object> metadata) {
        JPActivityModel activity = JPActivityModel.builder()
                .userId(userId)
                .organizationId(orgId)
                .type(type)
                .title(title)
                .content(content)
                .domain(domain)
                .metadata(metadata)
                .timestamp(Instant.now())
                .build();

        JPActivityModel saved = activityRepository.save(activity);
        
        // Broadcast to organization activity feed
        if (orgId != null) {
            realtimeBroadcaster.broadcastToOrganization(orgId, "activity", saved);
        }
        
        return saved;
    }
}
