package com.talentboozt.s_backend.domains.insights.service;

import com.talentboozt.s_backend.domains.insights.model.InsightModel;
import com.talentboozt.s_backend.domains.insights.repository.mongodb.InsightRepository;
import com.talentboozt.s_backend.shared.realtime.service.RealtimeBroadcaster;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InsightService {
    private final InsightRepository insightRepository;
    private final RealtimeBroadcaster realtimeBroadcaster;

    public InsightModel generateInsight(String orgId, String type, String title, String content, String impact, Map<String, Object> data) {
        InsightModel insight = InsightModel.builder()
                .organizationId(orgId)
                .type(type)
                .title(title)
                .content(content)
                .impact(impact)
                .dataPoints(data)
                .createdAt(Instant.now())
                .build();

        InsightModel saved = insightRepository.save(insight);
        
        // Broadcast to organization insights feed
        realtimeBroadcaster.broadcastToOrganization(orgId, "insights", saved);
        
        return saved;
    }

    public List<InsightModel> getOrgInsights(String orgId) {
        return insightRepository.findByOrganizationIdOrderByCreatedAtDesc(orgId);
    }
}
