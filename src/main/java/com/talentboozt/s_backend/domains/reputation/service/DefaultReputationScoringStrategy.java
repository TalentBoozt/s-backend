package com.talentboozt.s_backend.domains.reputation.service;

import com.talentboozt.s_backend.domains.reputation.model.ReputationSourceType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DefaultReputationScoringStrategy implements ReputationScoringStrategy {

    private final Map<ReputationSourceType, Integer> weights = Map.of(
            ReputationSourceType.ARTICLE_PUBLISH, 50,
            ReputationSourceType.ARTICLE_LIKE, 10,
            ReputationSourceType.ARTICLE_BOOKMARK, 10,
            ReputationSourceType.POST_CREATE, 20,
            ReputationSourceType.POST_UPVOTE, 5,
            ReputationSourceType.COMMENT_CREATE, 10,
            ReputationSourceType.COMMENT_UPVOTE, 5,
            ReputationSourceType.REACTION_RECEIVE, 2,
            ReputationSourceType.SPAM_PENALTY, -50);

    @Override
    public int getScoreFor(ReputationSourceType type) {
        return weights.getOrDefault(type, 0);
    }
}
